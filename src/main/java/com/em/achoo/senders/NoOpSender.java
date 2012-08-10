package com.em.achoo.senders;

import com.em.achoo.actors.interfaces.ISender;
import com.em.achoo.model.Message;
import com.em.achoo.model.interfaces.ISubscription;

public class NoOpSender implements ISender {

	@Override
	public void send(Message message) {
		//no op
	}

	@Override
	public void init(ISubscription subscription) {
		//no op
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}

}
