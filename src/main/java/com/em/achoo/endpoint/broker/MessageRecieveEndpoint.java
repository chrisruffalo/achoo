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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.util.Duration;

import com.em.achoo.actors.exchange.ExchangeManager;
import com.em.achoo.actors.interfaces.IExchangeManager;
import com.em.achoo.model.Message;
import com.google.common.io.ByteStreams;

@Path("/")
public class MessageRecieveEndpoint {

	private Logger log = LoggerFactory.getLogger(MessageRecieveEndpoint.class);
	
	@PUT
	@POST
	@Path("/send/{exchangeName}")
	@Consumes(value={MediaType.WILDCARD})
	public String send(@PathParam(value="exchangeName") String name, @Context HttpServletRequest request) {
		//create message
		Message message = Message.create();
		
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
			this.log.warn("Message does not contain a body.");
		}
		message.setBody(content);

		boolean result = this.dispatch(name, message);
		String response = "ok";
		if(!result) {
			response = "error";
		}
		
		//return response
		return response;
	}
	
	private boolean dispatch(String exchangeName, Message achooMessage) {
		if(achooMessage == null) {
			return false;
		}
		
		//create system and reference for dispatch actor
		IExchangeManager manager = ExchangeManager.get();
		
		//tell the dispatcher the message that was just recieved, and wait for future response
		Future<Boolean> managerDispatchFuture = manager.dispatch(exchangeName, achooMessage);
			
		boolean response = false;
		try {
			response = Await.result(managerDispatchFuture, Duration.Undefined());
		} catch (Exception e) {
			this.log.error("An exception occured while routing message '{}' to exchange '{}' with error = '{}'", new Object[]{achooMessage.getId(), exchangeName, e.getMessage()});
			response = false;
		}
		
		//return result
		return response;
	}
}
