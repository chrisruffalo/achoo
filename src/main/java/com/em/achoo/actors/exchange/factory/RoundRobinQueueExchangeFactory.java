package com.em.achoo.actors.exchange.factory;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.em.achoo.actors.exchange.RoundRobinQueueExchange;
import com.em.achoo.model.subscription.Subscription;
import com.google.common.collect.Iterators;

import akka.actor.Actor;
import akka.actor.ActorContext;
import akka.agent.Agent;

public class RoundRobinQueueExchangeFactory extends AbstractExchangeFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Agent<Collection<Subscription>> subscriberAgent = null;
	Agent<Iterator<Subscription>> iteratorAgent = null;

	public RoundRobinQueueExchangeFactory(ActorContext context) {
		super(context);
		
		Collection<Subscription> subscriberBase = new LinkedList<Subscription>();
		
		this.subscriberAgent = new Agent<Collection<Subscription>>(subscriberBase, this.getContext().system());
		this.iteratorAgent = new Agent<Iterator<Subscription>>(Iterators.cycle(subscriberBase), this.getContext().system());
	}

	@Override
	public Actor create() {
		return new RoundRobinQueueExchange(this.subscriberAgent, this.iteratorAgent);
	}

}
