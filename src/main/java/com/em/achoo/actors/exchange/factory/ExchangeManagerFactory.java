package com.em.achoo.actors.exchange.factory;

import akka.actor.Actor;
import akka.actor.UntypedActorFactory;

import com.em.achoo.actors.exchange.ExchangeManager;
import com.em.achoo.actors.exchange.storage.IExchangeStorage;

public class ExchangeManagerFactory implements UntypedActorFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IExchangeStorage storage = null;
	
	public ExchangeManagerFactory(IExchangeStorage storage) {
		this.storage = storage;
	}
	
	@Override
	public Actor create() {
		return new ExchangeManager(this.storage);
	}

}
