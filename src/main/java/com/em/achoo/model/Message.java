package com.em.achoo.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.em.achoo.model.exchange.ExchangeInformation;

/**
 * Internal message object inspired by JBoss ESBs message concept.  Includes an ID, simple headers and parameter support,
 * as well as a central body object.
 * 
 * @author chris
 *
 */
public class Message {
	

	private Map<String, Object> headers = null;
	
	private Map<String, Object> parameters = null;
	
	private ExchangeInformation toExchange = null;
	
	private Object body = null;
	
	private String id = null;
	
	private Message() {
		this.headers = new HashMap<String, Object>(0);
		this.parameters = new HashMap<String, Object>(1);
	}
	
	public Object getBody() {
		return this.body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public String getId() {
		return id;
	}

	private void setId(String messageId) {
		this.id = messageId;
	}
		
	public ExchangeInformation getToExchange() {
		return toExchange;
	}

	private void setToExchange(ExchangeInformation toExchange) {
		this.toExchange = toExchange;
	}

	public static Message create() {
		Message message = new Message();
		
		String messageId = UUID.randomUUID().toString().toUpperCase();
		
		message.setId(messageId);
		
		return message;
	}
	
	public static Message create(ExchangeInformation toExchange) {
		Message message = Message.create();
		
		message.setToExchange(toExchange);
		
		return message;
	}
	
}
