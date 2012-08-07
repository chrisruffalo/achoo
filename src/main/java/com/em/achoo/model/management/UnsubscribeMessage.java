package com.em.achoo.model.management;

public class UnsubscribeMessage {

	private String exchangeName;
	
	private String subscriptionId;

	public String getExchangeName() {
		return exchangeName;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}	
	
}
