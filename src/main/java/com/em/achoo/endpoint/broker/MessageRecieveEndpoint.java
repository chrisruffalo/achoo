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

import com.em.achoo.endpoint.AbstractEndpoint;
import com.em.achoo.model.Message;
import com.em.achoo.model.exchange.ExchangeInformation;
import com.em.achoo.model.exchange.ExchangeType;
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
	 * Send a message to the named topic.
	 * 
	 * @throws WebApplicationException(404) if the exchange does not exist
	 * 
	 * @param name name of the topic
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
		ExchangeInformation exchange = new ExchangeInformation();
		exchange.setName(name);
		exchange.setType(ExchangeType.TOPIC);
	
		//send
		String response = this.send(exchange, request);
		
		//return response
		return response;
	}
	
	/**
	 * Send a message to the exchange of the given name and type.  
	 * 
	 * @throws WebApplicationException(404) if the exchange does not exist
	 * 
	 * @param name name of the exchange
	 * @param type type of exchange to send to (see: {@link ExchangeType})
	 * @param request
	 * @return
	 */
	@PUT
	@POST
	@Path("/send/{exchangeName}/{type}")
	@NoCache
	@Consumes(value={MediaType.WILDCARD})
	@Produces(value={MediaType.TEXT_PLAIN})
	public String send(@PathParam(value="exchangeName") String name, String typeString, @Context HttpServletRequest request) {
		//create message
		ExchangeInformation exchange = new ExchangeInformation();
		exchange.setName(name);
		exchange.setType(this.stringToExchangeType(typeString));		
		
		//send
		String response = this.send(exchange, request);
		
		//return response
		return response;
	}
	
	/**
	 * Send a message to any exchange type with the given name.
	 * 
	 * @throws WebApplicationException(404) if the exchange does not exist
	 * 
	 * @param name name of the exchange
	 * @param request
	 * @return
	 */
	@PUT
	@POST
	@Path("/send/{exchangeName}/any")
	@NoCache
	@Consumes(value={MediaType.WILDCARD})
	@Produces(value={MediaType.TEXT_PLAIN})
	public String sendAny(@PathParam(value="exchangeName") String name, @Context HttpServletRequest request) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		
		for(ExchangeType type : ExchangeType.values()) {
			//create message
			ExchangeInformation exchange = new ExchangeInformation();
			exchange.setName(name);
			exchange.setType(type);
			builder.append("{");
			builder.append(this.send(exchange, request));
			builder.append("}");
		}
		
		//close
		builder.append("]");
		
		//send
		String response = builder.toString();
		
		//return response
		return response;
	}
	
	private String send(ExchangeInformation exchange, HttpServletRequest request) {
		//create message for exchange
		Message message = Message.create(exchange);
		
		//each of the parameters should be put on the message
		Enumeration<String> parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			String value = request.getParameter(parameterName);
			
			if(value != null) {
				this.logger.info("Parameter: {} = {}", parameterName, value);
				message.getParameters().put(parameterName, value);
			}
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

		//set the message body to the binary content of the stream
		if(content.length > 0) {
			this.logger.info("Added message body of size {}", content.length);
			message.setBody(content);
		}

		this.dispatch(message);
		String response = "dispatched message";
		
		//return response
		return response;
	}
	
	/**
	 * Method used by different send end points to route the message
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
		ActorRef manager = this.getExchangeManager();
		
		//tell the dispatcher the message that was just received
		manager.tell(achooMessage);
	}
}
