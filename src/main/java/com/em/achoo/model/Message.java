package com.em.achoo.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Internal message object inspired by JBoss ESBs message concept.  Includes an ID, simple headers and parameter support,
 * as well as a central body object.
 * 
 * @author chris
 *
 */
public class Message {
	
	public static final String MESSAGE_BODY = "com.em.achoo.model.Message.body"; 

	private Map<String, Object> headers = null;
	
	private Map<String, Object> parameters = null;
	
	private String id = null;
	
	private Message() {
		this.headers = new HashMap<String, Object>(0);
		this.parameters = new HashMap<String, Object>(1);
	}
	
	public Object getBody() {
		return this.parameters.get(Message.MESSAGE_BODY);
	}

	public void setBody(Object body) {
		this.parameters.put(Message.MESSAGE_BODY, body);
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

	public static Message create() {
		Message message = new Message();
		
		String messageId = UUID.randomUUID().toString().toUpperCase();
		
		message.setId(messageId);
		
		return message;
	}
	
}
