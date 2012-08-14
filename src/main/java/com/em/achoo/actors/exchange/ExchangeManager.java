package com.em.achoo.actors.exchange;

import java.util.Collection;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.em.achoo.actors.exchange.storage.IExchangeStorage;
import com.em.achoo.model.Envelope;
import com.em.achoo.model.Message;
import com.em.achoo.model.exchange.ExchangeInformation;
import com.em.achoo.model.management.UnsubscribeMessage;
import com.em.achoo.model.subscription.NoOpSubscription;
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
	
	private IExchangeStorage storage = null;
	
	public ExchangeManager(IExchangeStorage storage) {
		this.storage = storage;
	}

	@Override
	public void onReceive(Object arg0) throws Exception {
		if(arg0 instanceof Message) {
			this.dispatch((Message)arg0);			
		} else if(arg0 instanceof Subscription) {
			Subscription result = this.subscribe((Subscription) arg0);
			this.sender().tell(result);
		} else if(arg0 instanceof UnsubscribeMessage) {
			boolean result = this.unsubscribe((UnsubscribeMessage) arg0);
			this.sender().tell(result);
		}
	}
	
	private void dispatch(Message message) {
		Collection<Subscription> exchangeSubscribers = this.storage.getNextSubscribers(message.getToExchange());
		
		if(exchangeSubscribers == null || exchangeSubscribers.isEmpty()) {
			this.logger.debug("No messges dispatched to empty {}", message.getId(), message.getToExchange());
			return;	
		}
		
		//get sender pool reference
		ActorRef senderPool = this.context().actorFor("/user/senders");
		if(senderPool == null || senderPool.isTerminated()) {
			if(senderPool == null) {
				this.logger.warn("No sender pool available at '/user/senders'.");
			} else {
				this.logger.warn("No sender pool available at '{}'", senderPool.path().toString());
			}
			return;
		}			
		
		//create and send an envelope to each subscriber
		for(Subscription subscriber : exchangeSubscribers) {
			Envelope envelope = new Envelope(subscriber, message);
			senderPool.tell(envelope);
		}
	}
	
	public Subscription subscribe(Subscription subscription) {		
		//update subscription, if required
		String subscriptionId = subscription.getId();
		if(subscriptionId == null || subscriptionId.isEmpty()) {
			subscriptionId = UUID.randomUUID().toString().toUpperCase();
			subscription.setId(subscriptionId);
		}		
		
		this.storage.add(subscription);
		
		return subscription;
	}
	
	
	public boolean unsubscribe(UnsubscribeMessage unsubscribe) {

		ExchangeInformation information = new ExchangeInformation();
	
		//create empty/no-ip subscription
		NoOpSubscription subscription = new NoOpSubscription();
		subscription.setId(unsubscribe.getSubscriptionId());
		subscription.setExchangeInformation(information);
		
		this.storage.remove(subscription);
		
		return true;
	}
	
}
