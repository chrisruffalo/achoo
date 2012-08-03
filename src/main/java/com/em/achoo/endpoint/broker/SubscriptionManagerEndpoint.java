package com.em.achoo.endpoint.broker;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.em.achoo.actors.exchange.ExchangeManager;
import com.em.achoo.model.Exchange;
import com.em.achoo.model.ExchangeType;
import com.em.achoo.model.HttpSendMethod;
import com.em.achoo.model.HttpSubscription;
import com.em.achoo.model.Subscription;
import com.em.achoo.model.SubscriptionType;

@Path("/")
public class SubscriptionManagerEndpoint {

	/**
	 * Subscribe to an on-demand topic.  Returns the subscription id.
	 * 
	 * 
	 * @param exchangeName - name of the exchange to subscribe to
	 * @param typeString - queue or topic mode
	 * @return
	 */
	@GET
	@PUT
	@POST
	@Path("/subscribe/{exchangeName}/{type}/demand")
	@Consumes(value={MediaType.WILDCARD})
	@Produces(value={MediaType.TEXT_PLAIN})
	public String subscribe(@PathParam(value="exchangeName") String exchangeName, @PathParam(value="type") String typeString) {

		Exchange exchange = new Exchange();
		exchange.setName(exchangeName);
		
		typeString = typeString == null ? "TOPIC" : typeString.toUpperCase();
		ExchangeType type = null;
		try {
			type = ExchangeType.valueOf(typeString);
		} catch (EnumConstantNotPresentException ex) {
			type = ExchangeType.TOPIC;
		}
		
		exchange.setType(type);
		
		Subscription subscription = new Subscription();
		subscription.setExchange(exchange);
		subscription.setType(SubscriptionType.ON_DEMAND);

		this.doSubscribe(subscription);
		
		return subscription.getId();
	}
	
	/**
	 * Subscribe to an http callback topic.  Returns the subscription id.
	 * 
	 * @param exchangeName - name of the exchange to subscribe to
	 * @param typeString - queue or topic mode
	 * @param host - host that the http callback is on
	 * @param port - port the http callback is on
	 * @param path - additional path information for the callback (where it should point)
	 * @param methodString - method (GET, PUT, POST, etc)
	 * @return
	 */
	@GET
	@PUT
	@POST
	@Path("/subscribe/{exchangeName}/{type}/http")
	@Consumes(value={MediaType.WILDCARD})
	@Produces(value={MediaType.TEXT_PLAIN})
	public String subscribeHttp(@PathParam(value="exchangeName") String exchangeName, @PathParam(value="type") String typeString, @QueryParam(value="host") String host, @QueryParam(value="port") @DefaultValue(value="-1") int port, @QueryParam(value="path") @DefaultValue(value="/") String path, @QueryParam(value="method") @DefaultValue(value="PUT") String methodString) {

		Exchange exchange = new Exchange();
		exchange.setName(exchangeName);
		
		if(null == host || port < 0) {
			return "host or port was not specified";
		}
		
		typeString = typeString == null ? "TOPIC" : typeString.toUpperCase();
		ExchangeType type = null;
		try {
			type = ExchangeType.valueOf(typeString);
		} catch (EnumConstantNotPresentException ex) {
			type = ExchangeType.TOPIC;
		}
		
		exchange.setType(type);
		
		HttpSubscription subscription = new HttpSubscription();
		subscription.setExchange(exchange);
		subscription.setHost(host);
		subscription.setPort(port);
		subscription.setPath(path);

		HttpSendMethod method = null;
		try {
			method = HttpSendMethod.valueOf(methodString);
		} catch (EnumConstantNotPresentException ex) {
			method = HttpSendMethod.PUT;
		}
		subscription.setMethod(method);
		
		this.doSubscribe(subscription);
		
		return subscription.getId();
	}
	

	/**
	 * Unsubscribe from a given exchange.  TOPIC/QUEUE type does not matter here.
	 * 
	 * @param exchangeName - name of the exchange to subscribe to
	 * @param subscriptionId - the subscription id that was returned when a subscription was created
	 * @return
	 */
	@GET
	@PUT
	@POST
	@Path("/unsubscribe/{exchangeName}/{subscriptionId}")
	@Consumes(value={MediaType.WILDCARD})
	@Produces(value={MediaType.TEXT_PLAIN})
	public String unsubscribe(@PathParam(value="exchangeName") String exchangeName, @PathParam(value="subscriptionId") String subscriptionId) {
		if(exchangeName == null || exchangeName.isEmpty() || subscriptionId == null || subscriptionId.isEmpty()) {
			throw new WebApplicationException(Response.status(404).entity("no exchange or subscriptionid given").build());
		}		
		return Boolean.toString(this.doUnsubscribe(exchangeName, subscriptionId));
	}
	
	
	private boolean doUnsubscribe(String exchangeName, String subscriptionId) {
		boolean result = ExchangeManager.get().unsubscribe(exchangeName, subscriptionId);
		
		return result;
	}

	private Subscription doSubscribe(Subscription subscription) {
		subscription = ExchangeManager.get().subscribe(subscription);
		return subscription;
	}
	
}
