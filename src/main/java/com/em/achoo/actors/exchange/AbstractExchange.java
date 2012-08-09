package com.em.achoo.actors.exchange;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.runtime.AbstractFunction1;
import akka.actor.UntypedActor;
import akka.agent.Agent;
import akka.util.Timeout;

import com.em.achoo.model.MailBag;
import com.em.achoo.model.Message;
import com.em.achoo.model.management.UnsubscribeMessage;
import com.em.achoo.model.subscription.NoOpSubscription;
import com.em.achoo.model.subscription.Subscription;

public abstract class AbstractExchange extends UntypedActor {

	private Logger logger = LoggerFactory.getLogger(AbstractExchange.class);
	
	private Agent<Collection<Subscription>> subscribers = null; 
	
	public AbstractExchange(Agent<Collection<Subscription>> subscribers) {
		this.subscribers = subscribers;
	}
	
	@Override
	public void onReceive(Object arg0) throws Exception {
		
		if(arg0 instanceof Subscription) {
			this.addSubscriber((Subscription) arg0);
		} else if(arg0 instanceof Message) {
			//collect recipients for the message			
			Collection<Subscription> recipients = this.getRecipientsForMessage((Message) arg0);
			
			//put them in a mailbag
			MailBag bag = new MailBag();
			bag.setSubscriptions(recipients);
			bag.setMessage((Message) arg0);
			
			this.logger.trace("Telling {} about mailbag with {} recipients (message {})", new Object[]{this.sender().path().toString(), bag.getSubscriptions().size(), bag.getMessage().getId()});
			
			//reply to the sender with the bag
			this.sender().tell(bag);
		} else if(arg0 instanceof UnsubscribeMessage) {
			this.removeSubscriber((UnsubscribeMessage) arg0);
		}
	}
	
	/**
	 * Atomically update subscribers
	 * 
	 * @param subscriber
	 */
	protected void addSubscriber(final Subscription subscriber) {
		this.subscribers.send(new AbstractFunction1<Collection<Subscription>, Collection<Subscription>>() {
			@Override
			public Collection<Subscription> apply(Collection<Subscription> arg0) {
				arg0.add(subscriber);
				return arg0;
			}
		});
		this.logger.trace("Added subscriber: {} (now {})", subscriber.getId(), this.subscribers.get().size());
	}
	
	protected void removeSubscriber(final UnsubscribeMessage subscriber) {
		this.subscribers.send(new AbstractFunction1<Collection<Subscription>, Collection<Subscription>>() {
			@Override
			public Collection<Subscription> apply(Collection<Subscription> arg0) {
				Subscription removalTemplate = new NoOpSubscription();
				removalTemplate.setId(subscriber.getSubscriptionId());
				arg0.remove(removalTemplate);
				return arg0;
			}
		});
		Collection<Subscription> subscribers = this.getSubscribers();
		this.logger.trace("Removed subscriber: {} (now {})", subscriber.getSubscriptionId(), subscribers.size());
	}
	
	/**
	 * Get current subscribers
	 * 
	 * @return
	 */
	protected Collection<Subscription> getSubscribers() {
		return this.subscribers.await(Timeout.intToTimeout(1000));
	}
		
	protected abstract Collection<Subscription> getRecipientsForMessage(Message message);
	
	
	
}
