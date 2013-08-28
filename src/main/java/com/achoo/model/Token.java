package com.achoo.model;

import java.util.Date;

import com.lmax.disruptor.EventFactory;

public class Token extends Item {

	private String destinationKey;
	
	private String correlationId;
	
	private Date created;

	public Token() {
		// do not create date here, these items are pre-allocated
	}
	
	public String getDestinationKey() {
		return destinationKey;
	}

	public void setDestinationKey(String destinationKey) {
		this.destinationKey = destinationKey;
	}
		
	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public static EventFactory<Token> getTokenFactory() {
		return new EventFactory<Token>() {
			@Override
			public Token newInstance() {
				return new Token();
			}			
		};
	}

}
