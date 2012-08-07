package com.em.achoo.actors.sender.factory;

import akka.actor.Actor;
import akka.actor.UntypedActorFactory;

import com.em.achoo.actors.interfaces.ISender;
import com.em.achoo.actors.sender.SenderActor;
import com.em.achoo.model.subscription.Subscription;

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
		ISender sender = this.subscription.createSender();
		SenderActor actor = new SenderActor(sender);	
		return actor;
	}
	
}
