package com.em.achoo.actors.sender;

import com.em.achoo.model.Message;
import com.em.achoo.model.subscription.Subscription;

import akka.actor.UntypedActor;

public abstract class AbstractSender extends UntypedActor {

	protected abstract void send(Message message);
		
	private Subscription subscription = null;
	
	private AbstractSender() {
		
	}
	
	public AbstractSender(Subscription subscription) {
		this();
		
		this.subscription = subscription;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		
		if(message instanceof Message) {
			this.send((Message)message);
		}
		
	}	
	
	protected Subscription getSubscription() {
		return this.subscription;
	}
	
}
