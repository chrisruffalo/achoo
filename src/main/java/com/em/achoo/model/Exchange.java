package com.em.achoo.model;

import com.em.achoo.model.interfaces.IExchange;

public class Exchange implements IExchange {

	private String name = null;
	
	private ExchangeType type = ExchangeType.TOPIC;
	
	public void setName(String name) {
		this.name = name;
	}

	public void setType(ExchangeType type) {
		this.type = type;
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public ExchangeType getType() {
		return this.type;
	}

	
	
}
