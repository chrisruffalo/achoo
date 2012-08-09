package com.em.achoo.actors.exchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import akka.agent.Agent;

import com.em.achoo.model.Message;
import com.em.achoo.model.management.UnsubscribeMessage;
import com.em.achoo.model.subscription.Subscription;

public class RoundRobinQueueExchange extends AbstractExchange {
	
	public RoundRobinQueueExchange(Agent<Collection<Subscription>> subscribers) {
		super(subscribers);
	}

	@Override
	protected Collection<Subscription> getRecipientsForMessage(Message message) {
		final Collection<Subscription> subscriptions = this.getSubscribers();
		final Collection<Subscription> recipients = new ArrayList<Subscription>(1);
		
		if(!subscriptions.isEmpty()) {
			Subscription nextItem = null;
			
			if(subscriptions instanceof Queue) {
				//get subscription from current queue
				Queue<Subscription> sQueue = (Queue<Subscription>)subscriptions;
				nextItem = sQueue.poll();
			} else if(subscriptions instanceof List) {
				//use list semantics to do the same thing
				List<Subscription> sList = (List<Subscription>)subscriptions;
				nextItem = sList.remove(0);
			} else {
				//use non-list semantics to access iterator directly
				nextItem = subscriptions.iterator().next();
			}

			//remove subscreiption, then add subscription back
			if(nextItem != null) {
				//put subscriber into recipients
				recipients.add(nextItem);
				
				//remove subscriber from queue/list/collection
				UnsubscribeMessage unsub = new UnsubscribeMessage();
				unsub.setExchangeName(nextItem.getExchange().getName());
				unsub.setSubscriptionId(nextItem.getId());
				this.removeSubscriber(unsub);
				
				//return subscription to queue
				this.addSubscriber(nextItem);			
			}

		}
		
		return Collections.unmodifiableCollection(recipients);
	}	
	
}
