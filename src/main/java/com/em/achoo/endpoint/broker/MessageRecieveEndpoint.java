package com.em.achoo.endpoint.broker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.cache.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;

import com.em.achoo.actors.exchange.ExchangeManager;
import com.em.achoo.endpoint.AbstractEndpoint;
import com.em.achoo.model.Message;
import com.em.achoo.model.exchange.Exchange;
import com.google.common.io.ByteStreams;

/**
 * Class that publishes different endpoints for different types of message retrieve
 * 
 * @author chris
 *
 */
@Path("/")
public class MessageRecieveEndpoint extends AbstractEndpoint {

	private Logger logger = LoggerFactory.getLogger(MessageRecieveEndpoint.class);
	
	/**
	 * Send a message to the given exchange.  
	 * 
	 * @throws WebApplicationException(404) if the exchange does not exist
	 * 
	 * @param name name of the exchange
	 * @param request
	 * @return
	 */
	@PUT
	@POST
	@Path("/send/{exchangeName}")
	@NoCache
	@Consumes(value={MediaType.WILDCARD})
	@Produces(value={MediaType.TEXT_PLAIN})
	public String send(@PathParam(value="exchangeName") String name, @Context HttpServletRequest request) {
		//create message
		Exchange exchange = new Exchange();
		exchange.setName(name);
		Message message = Message.create(exchange);
		
		//each of the parameters should be put on the message
		Enumeration<String> parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			String value = request.getParameter(parameterName);
			message.getParameters().put(parameterName, value);
		}
		
		//get message content and put in byte array to store in object
		byte[] content = new byte[0];
		try {
			InputStream requestInputStream = request.getInputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(request.getContentLength());
			ByteStreams.copy(requestInputStream, outputStream);
			content = outputStream.toByteArray();
		} catch (IOException e) {
			this.logger.warn("message {} does not contain a body.", message.getId());
		}
		message.setBody(content);

		this.dispatch(message);
		String response = "dispatched message";
		
		//return response
		return response;
	}
	
	/**
	 * Method used by different send endpoints to route the message
	 * 
	 * @param exchangeName
	 * @param achooMessage
	 * @return
	 */
	private void dispatch(Message achooMessage) {
		if(achooMessage == null) {
			return;
		}
		
		//create system and reference for dispatch actor
		ActorRef manager = ExchangeManager.get(this.getActorSystem());
		
		//tell the dispatcher the message that was just received
		manager.tell(achooMessage);
	}
}
