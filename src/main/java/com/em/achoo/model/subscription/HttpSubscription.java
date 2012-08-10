package com.em.achoo.model.subscription;

import com.em.achoo.actors.interfaces.ISender;
import com.em.achoo.senders.HttpSender;

public class HttpSubscription extends Subscription {

	private String host = null;
	
	private int port = -1;
	
	private String path = null;
	
	private HttpSendMethod method = null;
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public HttpSendMethod getMethod() {
		return method;
	}

	public void setMethod(HttpSendMethod method) {
		this.method = method;
	}

	@Override
	public ISender createSender() {
		HttpSender sender = new HttpSender();
		sender.init(this);
		return sender;
	}
	
}
