package com.em.achoo.actors.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.UntypedActor;

import com.em.achoo.actors.interfaces.ISender;
import com.em.achoo.model.Envelope;
import com.em.achoo.model.Message;
import com.em.achoo.model.subscription.Subscription;

public class SenderActor extends UntypedActor {

	private Logger logger = LoggerFactory.getLogger(SenderActor.class);

	@Override
	public void onReceive(Object object) throws Exception {		
		if(object instanceof Envelope) {
			Message message = ((Envelope) object).getMessage();
			Subscription recipient = ((Envelope) object).getRecipient();

			ISender sender = recipient.createSender();
			
			this.logger.trace("Sending {} to {} on exchange {} with impl {} from path {}", new Object[]{message.getId(), sender.getName(), this.self().path().toString(), sender.getClass().getName(), this.self().path().toString()});
			
			sender.send(message);
		}		
	}	
}
