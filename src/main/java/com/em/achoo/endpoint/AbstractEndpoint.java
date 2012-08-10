package com.em.achoo.endpoint;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import com.em.achoo.Achoo;
import com.em.achoo.actors.exchange.ExchangeManager;
import com.em.achoo.configure.ConfigurationKeys;

public class AbstractEndpoint {

	@Context
	private ServletContext context;	
	
	protected ServletContext getContext() {
		return this.context;
	}
	
	protected ActorSystem getActorSystem() {
		return this.getAchooReference().getAchooActorSystem().getSystem();
	}
	
	protected Achoo getAchooReference() {
		ServletContext servletContext = this.getContext();
		
		Achoo achoo = (Achoo)servletContext.getAttribute(ConfigurationKeys.ACHOO_REFERENCE.toString());

		return achoo;
	}
	
	protected ActorRef getExchangeManager() {
		return ExchangeManager.get(this.getActorSystem());
	}
	
}
