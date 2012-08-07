package com.em.achoo.actors.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.UntypedActor;

import com.em.achoo.actors.interfaces.ISender;
import com.em.achoo.model.Message;

public class SenderActor extends UntypedActor {

	private ISender senderImpl = null;
	
	private Logger logger = LoggerFactory.getLogger(AbstractSender.class);
	
	public SenderActor(ISender sender) {
		this.senderImpl = sender;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {		
		if(message instanceof Message) {
			this.logger.debug("Sending {} to {} on exchange {} with impl {}", new Object[]{((Message) message).getId(), this.senderImpl.getName(), this.self().path().toString(), this.senderImpl.getClass().getName()});
			this.senderImpl.send((Message) message);
		}		
	}	
}
