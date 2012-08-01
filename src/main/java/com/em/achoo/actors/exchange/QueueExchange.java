package com.em.achoo.actors.exchange;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.collection.Iterable;
import scala.collection.Iterator;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;

import com.em.achoo.actors.sender.factory.SubscriptionSenderFactory;
import com.em.achoo.model.Message;
import com.em.achoo.model.Subscription;
import com.em.achoo.model.UnsubscribeMessage;

public class QueueExchange extends UntypedActor {

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
				
				//update reference
				ActorRef router = this.context().actorFor("router");
				if(router != null && !router.isTerminated()) {
					//wait for kill message
					this.context().watch(router);
					
					//kill router
					this.context().stop(router);					
				} else  {
					//if no router is running,  update router
					this.updateRouter();
				}
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
			//get routed ref
			ActorRef router = this.context().actorFor("router");

			//send message to router
			router.tell(message);
			
			//log send
			this.logger.info("forwarded message: {} to queued subscriber through: {}", ((Message) message).getId(), router.path().toString());
		} else if(message instanceof Terminated) {
			Terminated t = (Terminated)message;
			
			//only worry about router terminations
			if(!"router".equalsIgnoreCase(t.getActor().path().elements().last())) {
				return;
			}
			
			this.updateRouter();
		} else {
			this.logger.info("Got message of type: {} and content: {}", message.getClass().getName(), message.toString());
		}
		
	}
	
	private void updateRouter() {
		ActorRef router = this.context().actorFor("router");
		if(router != null && !router.isTerminated()) {
			//this is bad
			return;
		}
		//get children and copy paths
		Iterable<ActorRef> refs = this.context().children();
		List<String> refPathList = new ArrayList<String>();
		Iterator<ActorRef> it = refs.iterator();
		while(it.hasNext()) {
			ActorRef ref = it.next();
			refPathList.add(ref.path().toString());
			this.logger.info("Adding {} to ref path for mailbox", ref.path().toString());		
		}
		//create new router
		router = this.context().actorOf(new  Props().withRouter(new RoundRobinRouter(refPathList)), "router");
		//log
		this.logger.info("Created new router at: {}", router.path().toString());
	}

}
