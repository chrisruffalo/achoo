package com.em.achoo.model.subscription;

import akka.actor.ActorRef;

import com.em.achoo.actors.interfaces.ISender;
import com.em.achoo.sender.TestSender;

public class TestSubscription extends Subscription {

	private ActorRef ref = null;
	
	public TestSubscription(ActorRef ref) {
		this.ref = ref;
	}
	
	@Override
	public ISender createSender() {
		ISender sender = new TestSender(this.ref);
		sender.init(this);
		return sender;
	}

}
