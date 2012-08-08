package com.em.achoo.actors.exchange.factory;

import akka.actor.ActorContext;
import akka.actor.UntypedActorFactory;

public abstract class AbstractExchangeFactory implements UntypedActorFactory {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ActorContext context = null;
	
	public AbstractExchangeFactory(ActorContext context) {
		this.context = context;
	}
	
	protected ActorContext getContext() {
		return this.context;
	}

}
