package com.em.achoo.actors.exchange.storage.impl.memory;

import java.util.Collection;

import com.em.achoo.actors.exchange.storage.IExchange;
import com.em.achoo.model.exchange.ExchangeInformation;
import com.em.achoo.model.subscription.Subscription;


public abstract class AbstractInMemoryExchange<T extends Collection<Subscription>> implements IExchange {

	private ExchangeInformation info = null;
	
	private T backingCollection = null;
	
	protected abstract T constructBackingSubscriptionList();
	
	public AbstractInMemoryExchange(ExchangeInformation exchangeInfo) {
		//set exchange information
		this.info = exchangeInfo;
		
		//construct backing exchange
		this.backingCollection = this.constructBackingSubscriptionList();
	}	
	
	protected T getWholeSubscriberList() {
		return this.backingCollection;
	}
	
	@Override
	public ExchangeInformation getExchangeInformation() {
		return this.info;
	}

	@Override
	public void addSubscriber(Subscription subscription) {
		this.backingCollection.add(subscription);
		
	}

	@Override
	public void removeSubscriber(Subscription subscription) {
		this.backingCollection.remove(subscription);	
	}
	
	
}
