package com.em.achoo.actors.exchange;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import akka.agent.Agent;

import com.em.achoo.model.Message;
import com.em.achoo.model.subscription.Subscription;
import com.google.common.collect.Iterators;

public class RoundRobinQueueExchange extends AbstractExchange {
	
	private Agent<Iterator<Subscription>> subscriberIterator = null;

	public RoundRobinQueueExchange(Agent<Collection<Subscription>> subscribers, Agent<Iterator<Subscription>> iterator) {
		super(subscribers);
		this.subscriberIterator = iterator;
	}

	@Override
	protected void addSubscriber(Subscription subscriber) {
		//add subscriber as usual
		super.addSubscriber(subscriber);
		//update iterator
		this.subscriberIterator.send(Iterators.cycle(this.getSubscribers()));
	}

	@Override
	protected Collection<Subscription> getRecipientsForMessage(Message message) {
		Iterator<Subscription> it = this.subscriberIterator.get();
		Collection<Subscription> recipients = Collections.singleton(it.next());		
		return recipients;
	}	
	
}
