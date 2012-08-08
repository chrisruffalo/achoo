package com.em.achoo.actors.exchange.factory;

import java.util.Collection;
import java.util.LinkedList;

import akka.actor.Actor;
import akka.actor.ActorContext;
import akka.agent.Agent;

import com.em.achoo.actors.exchange.BroadcastTopicExchange;
import com.em.achoo.model.subscription.Subscription;

public class BroadcastTopicExchangeFactory extends AbstractExchangeFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Agent<Collection<Subscription>> subscriberAgent = null;

	public BroadcastTopicExchangeFactory(ActorContext context) {
		super(context);
		
		Collection<Subscription> subscriberBase = new LinkedList<Subscription>();
		
		this.subscriberAgent = new Agent<Collection<Subscription>>(subscriberBase, this.getContext().system());
	}

	@Override
	public Actor create() {
		return new BroadcastTopicExchange(this.subscriberAgent);
	}

}
