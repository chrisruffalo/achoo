package com.em.achoo.actors.exchange.factory;

import akka.actor.Actor;
import akka.actor.ActorSystem;
import akka.actor.UntypedActorFactory;
import akka.agent.Agent;

import com.em.achoo.actors.exchange.QueueExchange;

public class QueueExchangeRoutingFactory implements UntypedActorFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Agent<String> routerIdAgent = null;
	
	public QueueExchangeRoutingFactory(ActorSystem system) {
		this.routerIdAgent = new Agent<String>(null, system);
	}
	
	@Override
	public Actor create() {
		return new QueueExchange(this.routerIdAgent);
	}

}
