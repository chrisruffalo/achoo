package com.em.achoo.actors.factory;

import akka.actor.Actor;
import akka.actor.UntypedActorFactory;

import com.em.achoo.Achoo;
import com.em.achoo.actors.AchooManager;

public class AchooManagerFactory implements UntypedActorFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Achoo achooInstance = null;
	
	private AchooManagerFactory() {
		
	}
	
	public AchooManagerFactory(Achoo achoo) {
		this();
		
		this.achooInstance = achoo;
	}
	
	@Override
	public Actor create() {
		AchooManager manager = new AchooManager(this.achooInstance);
		
		return manager;
	}

}
