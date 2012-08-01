package com.em.achoo.actors.exchange;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.collection.Iterable;
import scala.collection.Iterator;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.agent.Agent;
import akka.routing.RoundRobinRouter;
import akka.util.Timeout;

import com.em.achoo.actors.sender.factory.SubscriptionSenderFactory;
import com.em.achoo.model.Message;
import com.em.achoo.model.Subscription;
import com.em.achoo.model.UnsubscribeMessage;

public class QueueExchange extends UntypedActor {
	
	private static final String ROUTER_PREFIX = "router-";
	private static final String SUBSCRIPTION_PREFIX = "subscription-";
	
	private Agent<String> routerIdAgent = null;

	private Logger logger = LoggerFactory.getLogger(TopicExchange.class);
	
	public QueueExchange(Agent<String> routerIdAgent) {
		this.routerIdAgent = routerIdAgent;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		
		if(message instanceof Subscription) {
			//look up subscriber and, if it doesn't exist, create it
			ActorRef subscriber = this.context().actorFor(((Subscription) message).getId());
			if(subscriber == null || subscriber.isTerminated()) {
				//create subscription name in akka hierarchy
				String subscriptionName	= QueueExchange.SUBSCRIPTION_PREFIX + ((Subscription) message).getId();
				
				//create a factory for the subscription sender
				SubscriptionSenderFactory factory = new SubscriptionSenderFactory((Subscription)message);
				Props senderProps = new Props(factory);
				subscriber = this.context().actorOf(senderProps, subscriptionName);
				
				this.logger.info("Created subscription for: {} on {} (at path: '{}')", new Object[]{((Subscription) message).getId(), ((Subscription) message).getExchange().getName(), subscriber.path().toString()});

				//get old router id to stop the router
				String oldRouterId = this.routerIdAgent.await(Timeout.intToTimeout(100));
				//manufacture new router id
				String newRouterId = UUID.randomUUID().toString().toUpperCase();
				
				//create new router
				this.createNewRouter(oldRouterId, newRouterId);
				
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
			
			//get router unique id
			String routerId = this.routerIdAgent.await(Timeout.intToTimeout(100));
			
			//get routed ref
			ActorRef router = this.context().actorFor(QueueExchange.ROUTER_PREFIX + routerId);

			//send message to router
			router.tell(message);
			
			//log send
			this.logger.info("forwarded message: {} to queued subscriber through: {}", ((Message) message).getId(), router.path().toString());
		} else {
			this.logger.info("Got message of type: {} and content: {}", message.getClass().getName(), message.toString());
		}
		
	}
	
	private void createNewRouter(String oldRouterId, String newRouterId) {

		//list of actor refs
		List<ActorRef> routedRefs = new ArrayList<ActorRef>();
		
		//get the actor references for the new router
		Iterable<ActorRef> children = this.context().children();
		Iterator<ActorRef> iterator = children.iterator();
		while(iterator.hasNext()) {
			ActorRef childRef = iterator.next();
			String end = childRef.path().name();
			
			//if it starts with the subscription prefix
			if(end.startsWith(QueueExchange.SUBSCRIPTION_PREFIX)) {
				routedRefs.add(childRef);
			}
		}
		
		//create the new router
		this.context().actorOf(new Props().withRouter(RoundRobinRouter.create(routedRefs)), QueueExchange.ROUTER_PREFIX + newRouterId);

		//send the update to the agent so that future messages 
		this.routerIdAgent.send(newRouterId);
		
		//make sure an old id exists:
		if(oldRouterId != null) {
			//lastly, stop the old router (may need to change this to poison pill?)
			ActorRef router = this.context().actorFor(QueueExchange.ROUTER_PREFIX + oldRouterId);
			if(router != null && !router.isTerminated()) {
				//kill router
				this.context().stop(router);					
			}
		}
	}

}
