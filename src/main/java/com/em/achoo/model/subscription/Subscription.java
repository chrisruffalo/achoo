package com.em.achoo.model.subscription;

import com.em.achoo.model.interfaces.IExchange;
import com.em.achoo.model.interfaces.ISubscription;

public abstract class Subscription implements ISubscription {

	private String id = null;
	
	private IExchange exchange = null;
	
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public IExchange getExchange() {
		return this.exchange;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setExchange(IExchange exchange) {
		this.exchange = exchange;
	}	
	
}
