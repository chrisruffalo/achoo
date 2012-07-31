package com.em.achoo.actors.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.em.achoo.actors.sender.factory.SubscriptionSenderFactory;
import com.em.achoo.model.Message;
import com.em.achoo.model.Subscription;
import com.em.achoo.model.UnsubscribeMessage;

public class TopicExchange extends UntypedActor {

	private Logger logger = LoggerFactory.getLogger(TopicExchange.class);
	
	@Override
	public void onReceive(Object message) throws Exception {

		if(message instanceof Subscription) {
			//look up subscriber and, if it doesn't exist, create it
			ActorRef subscriber = this.context().actorFor(((Subscription) message).getId());
			if(subscriber == null || subscriber.isTerminated()) {
				//create a factory for the subscription sender
				SubscriptionSenderFactory factory = new SubscriptionSenderFactory((Subscription)message);
				Props senderProps = new Props(factory);
				subscriber = this.context().actorOf(senderProps, ((Subscription) message).getId());
				
				this.logger.info("Created subscription for: {} on {} (at path: '{}')", new Object[]{((Subscription) message).getId(), ((Subscription) message).getExchange().getName(), subscriber.path().toString()});
			} else {
				this.logger.info("Subscription seems to exist for: {} on {} (at path: '{}')", new Object[]{((Subscription) message).getId(), ((Subscription) message).getExchange().getName(), subscriber.path().toString()});
			}
		} else if(message instanceof UnsubscribeMessage) {
			//lookup subscriber and kill it
			ActorRef subscriber = this.context().actorFor(((UnsubscribeMessage) message).getSubscriptionId());
			if(subscriber != null && !subscriber.isTerminated()) {
				subscriber.tell(PoisonPill.getInstance());
			}
			//if you just killed the last subscriber or no subscribers exist... (there has to be better non-race condition for this)
			if(this.context().children().size() <= 1) {
				this.logger.info("Shutting down exchange '{}'.", this.self().path().toString());
				this.getSelf().tell(PoisonPill.getInstance());
			}			
		} else if(message instanceof Message) {
			this.logger.info("got message: {} on exchange at: {}", ((Message) message).getId(), this.getSelf().path().toString());
			
			//select all children with wildcard
			ActorSelection select = this.context().actorSelection("*");
			
			//send message to children
			select.tell(message);
		}
	}
	

}
