package com.em.achoo.model.subscription;

import com.em.achoo.actors.interfaces.ISender;
import com.em.achoo.senders.OnDemandSender;

public class OnDemandSubscription extends Subscription {

	@Override
	public ISender createSender() {
		OnDemandSender sender = new OnDemandSender();
		sender.init(this);
		return sender;
	}

}
