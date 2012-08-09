package com.em.achoo.actors.exchange;

import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.agent.Agent;
import akka.routing.SmallestMailboxRouter;

import com.em.achoo.actors.exchange.factory.RoundRobinQueueExchangeFactory;
import com.em.achoo.actors.exchange.factory.BroadcastTopicExchangeFactory;
import com.em.achoo.actors.sender.SenderActor;
import com.em.achoo.model.Envelope;
import com.em.achoo.model.MailBag;
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

	public static final String SUBSCRIPTION_PREFIX = "subscription-";
	
	private Logger logger = LoggerFactory.getLogger(ExchangeManager.class);

	@Override
	public void onReceive(Object arg0) throws Exception {
		if(arg0 instanceof Message) {
			this.askForMailBag(((Message) arg0).getToExchange().getName(), (Message)arg0);			
		} else if(arg0 instanceof MailBag) {
			this.dispatch((MailBag)arg0);
		} else if(arg0 instanceof Subscription) {
			Subscription result = this.subscribe((Subscription) arg0);
			this.sender().tell(result);
		} else if(arg0 instanceof UnsubscribeMessage) {
			boolean result = this.unsubscribe((UnsubscribeMessage) arg0);
			this.sender().tell(result);
		}
	}
	
	private void dispatch(MailBag arg0) {
		ActorRef senderPool = this.context().actorFor("/user/senders");
		if(senderPool == null || senderPool.isTerminated()) {
			if(senderPool == null) {
				this.logger.warn("No sender pool available at '/user/senders'.");
			} else {
				this.logger.warn("No sender pool available at '{}'", senderPool.path().toString());
			}
			return;
		}
		
		//show that message is going to sender pool
		this.logger.trace("Sending message {} to sender pool to {} recipients", arg0.getMessage().getId(), arg0.getSubscriptions().size());
		
		//tell the sender pool router about each envelope from the mailbag
		for(Subscription subscription : arg0.getSubscriptions()) {
			Envelope envelope = new Envelope(subscription, arg0.getMessage());
			senderPool.tell(envelope);
		}
	}

	public void askForMailBag(String exchangeName, Message message) {
		ActorRef exchangeRef = this.context().actorFor(exchangeName);
		
		if(exchangeRef == null || exchangeRef.isTerminated()) {
			this.logger.debug("Could create recipeients for message '{}' on non-existant exchange '{}'", message.getId(), exchangeName);
			return;	
		}
		
		//send message to exchange (topic or queue) for further dispatch
		exchangeRef.tell(message, this.self());
	}

	
	public Subscription subscribe(Subscription subscription) {		
		IExchange exchange = subscription.getExchange();

		//update subscription
		subscription.setId(UUID.randomUUID().toString().toUpperCase());
		
		ActorRef dispatchRef = this.context().actorFor(exchange.getName());
		
		if(dispatchRef == null || dispatchRef.isTerminated()) {
			Props newExchangeProps = null;
			
			//create router based on type  			
			switch(exchange.getType()) {
				case TOPIC:
					newExchangeProps = new Props(new BroadcastTopicExchangeFactory(this.context()));
					break;
				case QUEUE:
					newExchangeProps = new Props(new RoundRobinQueueExchangeFactory(this.context()));
					break;
			}
			dispatchRef = this.context().actorOf(newExchangeProps, exchange.getName());
			
			this.logger.info("Created exchange: {} of type {} (at path: {})", new Object[]{exchange.getName(), exchange.getType(), dispatchRef.path().toString()});
		}
		
		//tell exchange to support given subscriber subscriber
		dispatchRef.tell(subscription);
		
		return subscription;
	}
	
	
	public boolean unsubscribe(UnsubscribeMessage unsubscribe) {
		ActorRef exchangeRef = this.context().actorFor(unsubscribe.getExchangeName());
		//if there is an actor for the given subscription, notify it to shutdown
		if(exchangeRef != null && !exchangeRef.isTerminated()) {
			exchangeRef.tell(unsubscribe);
			return true;
		}		
		return false;
	}
	
	private static Agent<ActorRef> manager = null;
	
	private static final String ACHOO_EXCHANGE_MANAGER_NAME = "exchange-manager";
	
	private static Semaphore managerLock = new Semaphore(1, true);
	
	public static ActorRef get(ActorSystem system) {

		if(manager == null) {
			try {
				managerLock.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(manager == null) {
				Props exchangeManagerProps = new Props(ExchangeManager.class).withRouter(new SmallestMailboxRouter(10));
				ActorRef newManager = system.actorOf(exchangeManagerProps, ExchangeManager.ACHOO_EXCHANGE_MANAGER_NAME);
				
				//create pool
				ActorRef senderPool = system.actorFor("senders"); 
				if(senderPool == null || senderPool.isTerminated()) {
					senderPool = system.actorOf(new Props(SenderActor.class).withRouter(new SmallestMailboxRouter(10)), "senders");
				}
				
				LoggerFactory.getLogger(ExchangeManager.class).info("Created sender pool at {}", senderPool.path().toString());
				
				manager = new Agent<ActorRef>(newManager, system);
			}
			
			managerLock.release();
		}		
	
		//return
		return manager.get();
	}
	
}
