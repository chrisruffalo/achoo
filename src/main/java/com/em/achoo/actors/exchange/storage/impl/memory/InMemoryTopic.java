package com.em.achoo.actors.exchange.storage.impl.memory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.em.achoo.model.exchange.ExchangeInformation;
import com.em.achoo.model.subscription.Subscription;

public class InMemoryTopic extends AbstractInMemoryExchange<List<Subscription>> {
	
	public InMemoryTopic(ExchangeInformation exchangeInfo) {
		super(exchangeInfo);
	}

	@Override
	public Collection<Subscription> getNextSubscribers() {
		return Collections.unmodifiableList(this.getWholeSubscriberList());
	}

	@Override
	protected List<Subscription> constructBackingSubscriptionList() {
		return new CopyOnWriteArrayList<Subscription>();
	}	

}
