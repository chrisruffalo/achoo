package com.em.achoo.server.impl;

import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;

import com.em.achoo.configure.IServerConfiguration;
import com.em.achoo.server.IServer;

public class TJWServerImpl implements IServer {

	private TJWSEmbeddedJaxrsServer embedded = null;
	
	public TJWServerImpl() {
		
	}
	
	@Override
	public void start(IServerConfiguration configuration) {
		//only start if server is not already created
		if(this.embedded != null) {
			return;
		}
			
		//create and configure tjws
		this.embedded = new TJWSEmbeddedJaxrsServer();
		this.embedded.setBindAddress(configuration.getBindAddress());
		this.embedded.setPort(configuration.getPort());
		
		//start container
		this.embedded.start();
		
		//add resources
		for(Class<?> resource : configuration.getRestEndpointClasses()) {
			this.embedded.getDeployment().getRegistry().addPerRequestResource(resource);
		}			
	}

	@Override
	public void stop() {
		if(this.embedded != null) {
			this.embedded.stop();
		}
	}

}
