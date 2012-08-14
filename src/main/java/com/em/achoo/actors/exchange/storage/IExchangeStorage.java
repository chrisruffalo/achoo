package com.em.achoo.actors.exchange.storage;

import java.util.Collection;

import com.em.achoo.model.exchange.ExchangeInformation;
import com.em.achoo.model.subscription.Subscription;

public interface IExchangeStorage {

	public void add(Subscription subscription);
	
	public void remove(Subscription subscription);
	
	public Collection<Subscription> getNextSubscribers(ExchangeInformation info);
	
}
