package com.em.achoo.model;

import java.util.Collection;

import com.em.achoo.model.subscription.Subscription;

public class MailBag {

	private Collection<Subscription> subscriptions = null;
	
	private Message message = null;

	public Collection<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(Collection<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
	
}
