package com.em.achoo.actors.sender;

import org.slf4j.LoggerFactory;

import com.em.achoo.model.Message;

public class OnDemandSender extends AbstractSender {

	@Override
	public void send(Message message) {
		LoggerFactory.getLogger(OnDemandSender.class).info("Message recieved by local (on-demand) subscription {}", this.getSubscription().getId());
	}

}
