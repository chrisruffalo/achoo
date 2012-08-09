package com.em.achoo.actors.exchange.factory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import akka.actor.Actor;
import akka.actor.ActorContext;
import akka.agent.Agent;

import com.em.achoo.actors.exchange.RoundRobinQueueExchange;
import com.em.achoo.model.subscription.Subscription;

public class RoundRobinQueueExchangeFactory extends AbstractExchangeFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Agent<Collection<Subscription>> subscriberAgent = null;

	public RoundRobinQueueExchangeFactory(ActorContext context) {
		super(context);
		
		Queue<Subscription> queue = new LinkedList<Subscription>();
		
		this.subscriberAgent = new Agent<Collection<Subscription>>(queue, this.getContext().system());
	}

	@Override
	public Actor create() {
		return new RoundRobinQueueExchange(this.subscriberAgent);
	}

}
