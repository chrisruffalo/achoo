package com.em.achoo.actors.exchange;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;

import com.em.achoo.model.Message;

public class TopicExchange extends AbstractExchange {

	//private Logger logger = LoggerFactory.getLogger(TopicExchange.class);

	@Override
	protected void postSubscribe(ActorRef ref) {
		// do nothing		
	}

	@Override
	protected ActorRef sendMessage(Message message) {
		//select all children with wildcard
		ActorSelection select = this.context().actorSelection("*");
		
		//send message to children
		select.tell(message);
		
		return select.target();
	}
	

}
