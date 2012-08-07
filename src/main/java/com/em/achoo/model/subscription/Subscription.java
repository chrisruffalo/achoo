package com.em.achoo.model.subscription;

import com.em.achoo.model.interfaces.IExchange;
import com.em.achoo.model.interfaces.ISubscription;

public class Subscription implements ISubscription {

	private String id = null;
	
	private SubscriptionType type = null;
	
	private IExchange exchange = null;
	
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public SubscriptionType getType() {
		return this.type;
	}

	@Override
	public IExchange getExchange() {
		return this.exchange;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(SubscriptionType type) {
		this.type = type;
	}

	public void setExchange(IExchange exchange) {
		this.exchange = exchange;
	}	
	
}
