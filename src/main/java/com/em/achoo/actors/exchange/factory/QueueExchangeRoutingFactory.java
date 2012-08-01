package com.em.achoo.actors.exchange.factory;

import akka.actor.Actor;
import akka.actor.UntypedActorFactory;

import com.em.achoo.actors.exchange.QueueExchange;

public class QueueExchangeRoutingFactory implements UntypedActorFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Actor create() {
		return new QueueExchange();
	}

}
