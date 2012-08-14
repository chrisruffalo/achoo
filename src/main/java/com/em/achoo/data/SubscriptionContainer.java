package com.em.achoo.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.em.achoo.model.exchange.ExchangeType;

@Entity(name="subscription")
public class SubscriptionContainer {

	@Id
	@Column(name="id")
	private String subscriptionId = null;
	
	@Column(name="exchangeName", nullable=false)
	private String exchangeName = null;
	
	@Column(name="exchangeType", nullable=false)
	private ExchangeType exchangeType = ExchangeType.TOPIC;
	
	@Column(name="binary")
	private byte[] subscriptionObjectBinary = new byte[0];
	
	/*============================================================================*/

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public byte[] getSubscriptionObjectBinary() {
		return subscriptionObjectBinary;
	}

	public void setSubscriptionObjectBinary(byte[] subscriptionObjectBinary) {
		this.subscriptionObjectBinary = subscriptionObjectBinary;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	public ExchangeType getExchangeType() {
		return exchangeType;
	}

	public void setExchangeType(ExchangeType exchangeType) {
		this.exchangeType = exchangeType;
	}	
	
}
