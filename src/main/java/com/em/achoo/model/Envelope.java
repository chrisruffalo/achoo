package com.em.achoo.model;

import com.em.achoo.model.subscription.Subscription;

public class Envelope {
	
	private Message message = null;
	
	private Subscription recipient = null;
	
	public Envelope(Subscription recipient, Message message) {
		this.recipient = recipient;
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}

	public Subscription getRecipient() {
		return recipient;
	}
}
