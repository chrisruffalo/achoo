package com.em.achoo.endpoint;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import com.em.achoo.Achoo;
import com.em.achoo.configure.ConfigurationKeys;
import com.em.achoo.model.exchange.ExchangeType;

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
		return this.getAchooReference().getExchangeManagerRef();
	}
			
	protected ExchangeType stringToExchangeType(String typeString) {
		typeString = typeString == null ? "TOPIC" : typeString.toUpperCase();
		ExchangeType type = null;
		try {
			type = ExchangeType.valueOf(typeString);
		} catch (EnumConstantNotPresentException ex) {
			type = ExchangeType.TOPIC;
		}
		return type;
	}	
}
