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
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.spi.NoLogWebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.util.Duration;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.em.achoo.endpoint.AbstractEndpoint;
import com.em.achoo.model.exchange.ExchangeInformation;
import com.em.achoo.model.exchange.ExchangeType;
import com.em.achoo.model.management.UnsubscribeMessage;
import com.em.achoo.model.subscription.HttpSendMethod;
import com.em.achoo.model.subscription.HttpSubscription;
import com.em.achoo.model.subscription.OnDemandSubscription;
import com.em.achoo.model.subscription.Subscription;

/**
 * Manages subscription information for Achoo subscribers.
 * 
 * @author chris
 *
 */
@Path("/")
public class SubscriptionManagerEndpoint extends AbstractEndpoint {

	private Logger logger = LoggerFactory.getLogger(MessageRecieveEndpoint.class);
	
	/**
	 * Subscribe to an on-demand topic.  Returns the subscription id.
	 * 
	 * 
	 * @param exchangeName name of the exchange to subscribe to
	 * @param typeString queue or topic mode
	 * @return subscription uuid
	 */
	@GET
	@PUT
	@POST
	@Path("/subscribe/{exchangeName}/{type}/demand")
	@NoCache
	@Consumes(value={MediaType.WILDCARD})
	@Produces(value={MediaType.TEXT_PLAIN})
	public String subscribe(@PathParam(value="exchangeName") String exchangeName, @PathParam(value="type") String typeString) {

		ExchangeInformation exchange = new ExchangeInformation();
		exchange.setName(exchangeName);
		
		typeString = typeString == null ? "TOPIC" : typeString.toUpperCase();
		ExchangeType type = null;
		try {
			type = ExchangeType.valueOf(typeString);
		} catch (EnumConstantNotPresentException ex) {
			type = ExchangeType.TOPIC;
		}
		
		exchange.setType(type);
		
		Subscription subscription = new OnDemandSubscription();
		subscription.setExchangeInformation(exchange);

		Subscription response = this.doSubscribe(subscription);
		
		return response.getId();
	}
	
	/**
	 * Subscribe to an http callback topic.  Returns the subscription id.
	 * 
	 * @param exchangeName name of the exchange to subscribe to
	 * @param typeString queue or topic mode
	 * @param host host that the http callback is on
	 * @param port port the http callback is on
	 * @param path additional path information for the callback (where it should point)
	 * @param methodString method (GET, PUT, POST, etc)
	 * @return subscription uuid
	 */
	@GET
	@PUT
	@POST
	@Path("/subscribe/{exchangeName}/{type}/http")
	@NoCache
	@Consumes(value={MediaType.WILDCARD})
	@Produces(value={MediaType.TEXT_PLAIN})
	public String subscribeHttp(@PathParam(value="exchangeName") String exchangeName, @PathParam(value="type") String typeString, @QueryParam(value="host") String host, @QueryParam(value="port") @DefaultValue(value="80") int port, @QueryParam(value="path") @DefaultValue(value="/") String path, @QueryParam(value="method") @DefaultValue(value="PUT") String methodString) {

		ExchangeInformation exchange = new ExchangeInformation();
		exchange.setName(exchangeName);
		
		if(null == host || port < 0) {
			return "host or port was not specified";
		}
		
		ExchangeType type = this.stringToExchangeType(typeString);
		
		exchange.setType(type);
		
		HttpSubscription subscription = new HttpSubscription();
		subscription.setExchangeInformation(exchange);
		subscription.setHost(host);
		subscription.setPort(port);
		
		if(path == null || path.isEmpty()) {
			path = "/";
		} else if(!path.startsWith("/")) {
			path = "/" + path;
		}
		
		subscription.setPath(path);

		HttpSendMethod method = null;
		try {
			if(methodString != null) {
				methodString = methodString.toUpperCase();
			}
			method = HttpSendMethod.valueOf(methodString);
		} catch (EnumConstantNotPresentException ex) {
			method = HttpSendMethod.PUT;
		}
		subscription.setMethod(method);
		
		Subscription response = this.doSubscribe(subscription);
		
		return response.getId();
	}	

	/**
	 * Unsubscribe from a given exchange.  TOPIC/QUEUE type does not matter here.
	 * 
	 * @param exchangeName name of the exchange to subscribe to
	 * @param subscriptionId the subscription id that was returned when a subscription was created
	 * @return subscription uuid
	 */
	@GET
	@PUT
	@POST
	@Path("/unsubscribe/{exchangeName}/{type}/{subscriptionId}")
	@NoCache
	@Consumes(value={MediaType.WILDCARD})
	@Produces(value={MediaType.TEXT_PLAIN})
	public String unsubscribe(@PathParam(value="exchangeName") String exchangeName, @PathParam(value="type") String typeString, @PathParam(value="subscriptionId") String subscriptionId) {
		if(exchangeName == null || exchangeName.isEmpty() || subscriptionId == null || subscriptionId.isEmpty()) {
			throw new WebApplicationException(Response.status(404).entity("no exchange or subscriptionid given").build());
		}		
		
		ExchangeType type = this.stringToExchangeType(typeString);
		
		ExchangeInformation information = new ExchangeInformation();
		information.setName(exchangeName);
		information.setType(type);		
		
		return Boolean.toString(this.doUnsubscribe(information, subscriptionId));
	}
	
	/**
	 * Implments the unsubscribe action, called by various unsubscribe methods so that they can focus on REST aspects
	 * 
	 * @param info the information for the exchange to unsubscribe from
	 * @param subscriptionId subscription uuid given when the original subscribe method was called
	 * @return
	 */
	private boolean doUnsubscribe(ExchangeInformation info, String subscriptionId) {
		UnsubscribeMessage message = new UnsubscribeMessage();
		
		message.setExchangeInformation(info);

		message.setSubscriptionId(subscriptionId);
		
		Future<Object> managerUnsubscribeFuture = Patterns.ask(this.getExchangeManager(), message, Timeout.intToTimeout(1000));
		
		boolean response = false;
		try {
			Object oResponse = Await.result(managerUnsubscribeFuture, Duration.Undefined());
			if(oResponse instanceof Boolean) {
				response = Boolean.valueOf((Boolean) oResponse);
			} else if(oResponse instanceof String) {
				response = Boolean.valueOf((String) oResponse);
			} else {
				response = false;
			}
		} catch (Exception e) {
			this.logger.error("An exception occured while unsubscribing from exchange '{}' with error = '{}'", new Object[]{info.toString(), e.getMessage()});
			response = false;
		}		
		
		return response;
	}

	/**
	 * Passes a subscription implementation (for various subscription types) to the exchange manager dispatch
	 * 
	 * @param subscription
	 * @return
	 */
	private Subscription doSubscribe(Subscription subscription) {
		Subscription subscriptionResponse = null;
		Future<Object> response = Patterns.ask(this.getExchangeManager(), subscription, Timeout.intToTimeout(1000));
		
		try {
			Object oResponse = Await.result(response, Duration.Undefined());
			if(oResponse instanceof Subscription) {
				subscriptionResponse = (Subscription)oResponse;
			}
		} catch (Exception e) {
			this.logger.error("An exception occured while subscribing to exchange '{}' with error = '{}'", new Object[]{subscription.getExchangeInformation().getName(), e.getMessage()});
			subscriptionResponse = null;
		}				
		
		if(subscriptionResponse == null) {
			throw new NoLogWebApplicationException(Response.status(Status.NOT_FOUND).entity("exception while subscribing").build());
		}
		
		return subscriptionResponse;
	}
}
