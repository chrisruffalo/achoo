package com.em.achoo.model.interfaces;

import com.em.achoo.model.SubscriptionType;

public interface ISubscription {

	public String getId();
	
	public SubscriptionType getType();
	
	public IExchange getExchange();
	
}
