package com.em.achoo.model.management;

import com.em.achoo.model.exchange.ExchangeInformation;

public class UnsubscribeMessage {

	private ExchangeInformation exchange;
	
	private String subscriptionId;

	public ExchangeInformation getExchangeInformation() {
		return exchange;
	}

	public void setExchangeInformation(ExchangeInformation exchange) {
		this.exchange = exchange;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}	
	
}
