package com.em.achoo.actors.sender.factory;

import com.em.achoo.actors.sender.HttpSender;
import com.em.achoo.actors.sender.LocalDeliverySender;
import com.em.achoo.model.subscription.Subscription;

import akka.actor.Actor;
import akka.actor.UntypedActorFactory;

public class SubscriptionSenderFactory implements UntypedActorFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Subscription subscription = null;
	
	private SubscriptionSenderFactory() {
		
	}
	
	public SubscriptionSenderFactory(Subscription subscription) {
		this();
		
		this.subscription = subscription;
	}

	@Override
	public Actor create() {
		Actor response = null;
		
		switch(this.subscription.getType()) {
			case HTTP_CALLBACK:
				response = new HttpSender(this.subscription);
				break;
			case ON_DEMAND:
				response = new LocalDeliverySender(this.subscription);
				break;
		}
		
		return response;
	}
	
}
