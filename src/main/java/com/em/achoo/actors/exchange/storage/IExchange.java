package com.em.achoo.actors.exchange.storage;

import java.util.Collection;

import com.em.achoo.model.exchange.ExchangeInformation;
import com.em.achoo.model.subscription.Subscription;

public interface IExchange {
	
	public ExchangeInformation getExchangeInformation();

	public void addSubscriber(Subscription subscription);
	
	public void removeSubscriber(Subscription subscription);
	
	public Collection<Subscription> getNextSubscribers();
	
}
