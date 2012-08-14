package com.em.achoo.actors.exchange.storage.impl.memory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.em.achoo.model.exchange.ExchangeInformation;
import com.em.achoo.model.subscription.Subscription;

public class InMemoryQueue extends AbstractInMemoryExchange<List<Subscription>> {

	private volatile int roundRobinIterator = 0;
	
	public InMemoryQueue(ExchangeInformation exchangeInfo) {
		super(exchangeInfo);
	}

	@Override
	public Collection<Subscription> getNextSubscribers() {
		
		//if the list is empty, return the empty set
		if(this.getWholeSubscriberList().isEmpty()) {
			return Collections.emptySet();
		}		
		
		Subscription sub = null;
		
		//get index and size of list
		int index = this.roundRobinIterator;
		int size = this.getWholeSubscriberList().size();
		
		//increment round robin iterator
		this.roundRobinIterator++;
		
		//create round robin index from iterator
		int modIndex = index % size;
		
		try {
			//get sub
			sub = this.getWholeSubscriberList().get(modIndex);
		} catch (IndexOutOfBoundsException ioobe) {
			
		}
		
		if(sub == null) {
			return Collections.emptySet();
		}
		
		return Collections.singleton(sub);
	}

	@Override
	protected List<Subscription> constructBackingSubscriptionList() {
		return new CopyOnWriteArrayList<Subscription>();
	}	
	
}
