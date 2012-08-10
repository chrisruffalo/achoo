package com.em.achoo.model.subscription;

import com.em.achoo.actors.interfaces.ISender;

public class NoOpSubscription extends Subscription {

	@Override
	public ISender createSender() {
		return null;
	}
	
}
