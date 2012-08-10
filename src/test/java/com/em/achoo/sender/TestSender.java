package com.em.achoo.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;

import com.em.achoo.model.Message;
import com.em.achoo.senders.AbstractSender;

public class TestSender extends AbstractSender {

	private ActorRef ref = null;
	
	private Logger logger = LoggerFactory.getLogger(TestSender.class);
	
	public TestSender(ActorRef ref) {
		this.ref = ref;
	}

	@Override
	public void send(Message message) {
		this.logger.trace("Sending message: {} (for accumulator at path: {})", message.getId(), this.ref.path().toString());
		
		this.ref.tell(message);
	}

}
