package com.em.achoo.actors.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.em.achoo.model.Message;
import com.em.achoo.model.Subscription;

public class LocalDeliverySender extends AbstractSender {

	private Logger logger = LoggerFactory.getLogger(LocalDeliverySender.class);
	
	public LocalDeliverySender(Subscription subscription) {
		super(subscription);
	}

	@Override
	public void send(Message message) {
		this.logger.info("Message recieved by local (on-demand) subscription {} on exchange {}", this.getSubscription().getId(), this.self().path().toString());
	}

}
