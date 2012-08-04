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

import com.em.achoo.actors.sender.factory.SubscriptionSenderFactory;
import com.em.achoo.model.Message;
import com.em.achoo.model.Subscription;
import com.em.achoo.model.UnsubscribeMessage;

/**
 * Abstract type that handles the backbone logic of both exchange types.  Mainly concerned with subscribe, unsubscribe, and message dispatch.
 * 
 * @author chris
 *
 */
public abstract class AbstractExchange extends UntypedActor {
	
	public static final String SUBSCRIPTION_PREFIX = "subscription-";
	
	public static final String ROUTER_PREFIX = "router-";
	
	private Logger logger = LoggerFactory.getLogger(AbstractExchange.class);
	
	/**
	 * Do some action after subscribing
	 * 
	 * @param ref
	 */
	protected abstract void postSubscribe(ActorRef ref);
	
	/**
	 * Send given message to child senders
	 * 
	 * @param message
	 * @return
	 */
	protected abstract ActorRef sendMessage(Message message);
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof Subscription) {
			//create subscription name in akka hierarchy
			String subscriptionName	= AbstractExchange.SUBSCRIPTION_PREFIX + ((Subscription) message).getId();
			
			//look up subscriber and, if it doesn't exist, create it
			ActorRef subscriber = this.context().actorFor(subscriptionName);
			if(subscriber == null || subscriber.isTerminated()) {
				
				//create a factory for the subscription sender
				SubscriptionSenderFactory factory = new SubscriptionSenderFactory((Subscription)message);
				Props senderProps = new Props(factory);
				subscriber = this.context().actorOf(senderProps, subscriptionName);
				
				//post subscribe action (really for queue manager so it can create router)
				this.postSubscribe(subscriber);
				
				this.logger.debug("Created subscription for: {} on {} (at path: '{}')", new Object[]{((Subscription) message).getId(), ((Subscription) message).getExchange().getName(), subscriber.path().toString()});
			} else {
				this.logger.debug("Subscription seems to exist for: {} on {} (at path: '{}')", new Object[]{((Subscription) message).getId(), ((Subscription) message).getExchange().getName(), subscriber.path().toString()});
			}
		} else if(message instanceof UnsubscribeMessage) {
			//get subscription name
			String subscriptionName	= AbstractExchange.SUBSCRIPTION_PREFIX + ((UnsubscribeMessage)message).getSubscriptionId();
			
			//lookup subscriber and kill it
			ActorRef subscriber = this.context().actorFor(subscriptionName);
			if(subscriber != null && !subscriber.isTerminated()) {
				
				//watch for terminate messages, so that we can unsubscribe with no children
				this.context().watch(subscriber);
				
				//terminate with poison pill in mailbox, we can do more after Terminate resolves
				this.context().stop(subscriber);
			}
		} else if(message instanceof Message) {
			//send message
			ActorRef sender = this.sendMessage((Message) message);
			
			//log send
			this.logger.debug("forwarded message: {} to queued subscriber through: {}", ((Message) message).getId(), sender.path().toString());
		} else if(message instanceof Terminated) {
			//when this is empty, poison yourself
			List<ActorRef> routedRefs = new ArrayList<ActorRef>();
			
			//if no subscribing children
			if(routedRefs.isEmpty()) {
				//shut down routers, just in case one is still active
				this.context().actorSelection(AbstractExchange.ROUTER_PREFIX + "-*").tell(PoisonPill.getInstance());
				//... shut self down
				this.self().tell(PoisonPill.getInstance());
			}			
		} else {
			this.logger.warn("Got unhandled of type: {} and content: {}", message.getClass().getName(), message.toString());
		}
		
	}

	@Override
	public void postStop() {
		//log stop so that we can be sure of shutdown
		this.logger.debug("Shut down exchange at path: {}", this.self().path().toString());

		//continue
		super.postStop();
	}

	/**
	 * Get the list of subscribing children, distinct from routing children
	 * 
	 * @return
	 */
	protected List<ActorRef> getSubscribingChildren() {
		return this.getPrefixChildren(AbstractExchange.SUBSCRIPTION_PREFIX);
	}
	
	/**
	 * Get the list of children that handle routing
	 * 
	 * @return
	 */
	protected List<ActorRef> getRoutingChildren() {
		return this.getPrefixChildren(AbstractExchange.ROUTER_PREFIX);
	}
	
	/**
	 * Iterates through children and pulls out those with a given prefix
	 * 
	 * @param prefix of the children to look at
	 * @return list of found children
	 */
	protected List<ActorRef> getPrefixChildren(String prefix) {
		//list of subscribing actor references
		List<ActorRef> routedRefs = new ArrayList<ActorRef>();
		
		//get the actor references for subscribed children
		Iterable<ActorRef> children = this.context().children();
		Iterator<ActorRef> iterator = children.iterator();
		while(iterator.hasNext()) {
			ActorRef childRef = iterator.next();
			String end = childRef.path().name();
			
			//if it starts with the subscription prefix
			if(end != null && end.toLowerCase().startsWith(AbstractExchange.SUBSCRIPTION_PREFIX)) {
				routedRefs.add(childRef);
			}
		}
		
		return routedRefs;
	}
	
}
