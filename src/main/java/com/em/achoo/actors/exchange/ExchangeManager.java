package com.em.achoo.actors.exchange;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.Broadcast;
import akka.routing.RoundRobinRouter;

import com.em.achoo.model.Message;
import com.em.achoo.model.interfaces.IExchange;
import com.em.achoo.model.management.UnsubscribeMessage;
import com.em.achoo.model.subscription.Subscription;

/**
 * Manages exchanges by routing subscription, unsubscribe, and message dispatches to implementations of the
 * various exchange types, who forward it to subscription types.
 * 
 * @author chris
 *
 */
public class ExchangeManager extends UntypedActor {

	public static final String ACHOO_EXCHANGE_MANAGER_NAME = "exchange-manager";
	
	public static final String SUBSCRIPTION_PREFIX = "subscription-";
	
	private Logger logger = LoggerFactory.getLogger(ExchangeManager.class);

	@Override
	public void onReceive(Object arg0) throws Exception {
		if(arg0 instanceof Message) {
			this.dispatch(((Message) arg0).getToExchange().getName(), (Message)arg0);			
		} else if(arg0 instanceof Subscription) {
			Subscription result = this.subscribe((Subscription) arg0);
			this.sender().tell(result);
		} else if(arg0 instanceof UnsubscribeMessage) {
			boolean result = this.unsubscribe((UnsubscribeMessage) arg0);
			this.sender().tell(result);
		}
	}
	
	private void dispatch(String exchangeName, Message message) {
		ActorRef exchangeRef = this.context().actorFor(exchangeName);
		
		if(exchangeRef == null || exchangeRef.isTerminated()) {
			this.logger.debug("Could not create recipients list for message '{}' on non-existant exchange '{}'", message.getId(), exchangeName);
			return;	
		}
		
		//send message to exchange (topic or queue) for further dispatch
		exchangeRef.tell(message, this.self());
	}
	
	public Subscription subscribe(Subscription subscription) {		
		IExchange exchange = subscription.getExchange();

		//update subscription, if required
		String subscriptionId = subscription.getId();
		if(subscriptionId == null || subscriptionId.isEmpty()) {
			subscriptionId = UUID.randomUUID().toString().toUpperCase();
			subscription.setId(subscriptionId);
		}		
		
		ActorRef dispatchRef = this.context().actorFor(exchange.getName());
		
		if(dispatchRef == null || dispatchRef.isTerminated()) {
			Props newExchangeProps = null;
			
			//create router based on type  			
			switch(exchange.getType()) {
				case TOPIC:
					newExchangeProps = new Props(BroadcastTopicTransactorExchange.class);
					break;
				case QUEUE:
					newExchangeProps = new Props(RoundRobinQueueTransactorExchange.class);
					break;
			}
			//create dispatch router and save properties
			newExchangeProps = newExchangeProps.withRouter(new RoundRobinRouter(5));
			
			//create actor ref
			dispatchRef = this.context().actorOf(newExchangeProps, exchange.getName());
			
			this.logger.info("Created exchange: {} of type {} (at path: {})", new Object[]{exchange.getName(), exchange.getType(), dispatchRef.path().toString()});
		}
		
		this.logger.trace("sending dispatch subscription to: {}", dispatchRef.path().toString());
		
		//tell exchange to support given subscriber subscriber
		dispatchRef.tell(new Broadcast(subscription));
		
		return subscription;
	}
	
	
	public boolean unsubscribe(UnsubscribeMessage unsubscribe) {
		ActorRef exchangeRef = this.context().actorFor(unsubscribe.getExchangeName());
		//if there is an actor for the given subscription, notify it to shutdown
		if(exchangeRef != null && !exchangeRef.isTerminated()) {
			exchangeRef.tell(new Broadcast(unsubscribe));
			return true;
		}		
		return false;
	}
	
}
