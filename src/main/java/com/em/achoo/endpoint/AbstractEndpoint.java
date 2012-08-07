package com.em.achoo.endpoint;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import com.em.achoo.actors.AchooActorSystem;
import com.em.achoo.actors.exchange.ExchangeManager;
import com.em.achoo.configure.ConfigurationKeys;

public class AbstractEndpoint {

	@Context
	private ServletContext context;	
	
	protected ServletContext getContext() {
		return this.context;
	}
	
	protected ActorSystem getActorSystem() {
		ServletContext servletContext = this.getContext();
		
		AchooActorSystem achooSystem = (AchooActorSystem)servletContext.getAttribute(ConfigurationKeys.ACHOO_ACTOR_SYSTEM.toString());
				
		return achooSystem.getSystem();
	}
	
	protected ActorRef getExchangeManager() {
		return ExchangeManager.get(this.getActorSystem());
	}
	
}
