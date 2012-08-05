package com.em.achoo.actors.exchange.factory;

import java.util.concurrent.Semaphore;

import akka.actor.Actor;
import akka.actor.ActorSystem;
import akka.actor.UntypedActorFactory;

import com.em.achoo.actors.exchange.QueueExchange;

public class QueueExchangeRoutingFactory implements UntypedActorFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Semaphore routerChangeSemaphore = null;
	
	public QueueExchangeRoutingFactory(ActorSystem system) {
		this.routerChangeSemaphore = new Semaphore(1, true);
	}
	
	@Override
	public Actor create() {
		return new QueueExchange(this.routerChangeSemaphore);
	}

}
