package com.em.achoo.server.impl;

import com.em.achoo.actors.AchooActorSystem;
import com.em.achoo.configure.IServerConfiguration;
import com.em.achoo.server.ServerType;
import com.typesafe.config.Config;

public class BasicServerConfiguration implements IServerConfiguration {

	private String bindAddress = null;
	
	private int port = 0;
	
	private Class<?>[] endpointClasses = null;
	
	private ServerType serverType = null;
	
	private Config rawConfiguration = null;
	
	private AchooActorSystem achooSystem = null;
	
	public BasicServerConfiguration() {
		this("0.0.0.0", 9090, ServerType.JETTY, new Class<?>[]{});
	}
	
	public BasicServerConfiguration(IServerConfiguration bootstrap) {
		if(bootstrap == null) {
			return;
		}
		
		this.bindAddress = bootstrap.getBindAddress();
		this.port = bootstrap.getPort();
		this.endpointClasses = bootstrap.getRestEndpointClasses();
		this.serverType = bootstrap.getServerType();
		
		if(this.endpointClasses == null) {
			this.endpointClasses = new Class<?>[]{};
		}
	}
	
	public BasicServerConfiguration(String bindAddress, int port, ServerType type, Class<?>[] endpointClasses) {
		this.bindAddress = bindAddress;
		this.port = port;
		this.endpointClasses = endpointClasses;
		this.serverType = type;
		
		if(this.endpointClasses == null) {
			this.endpointClasses = new Class<?>[]{};
		}
	}
	
	@Override
	public int getPort() {
		return this.port;
	}

	@Override
	public String getBindAddress() {
		return this.bindAddress;
	}

	@Override
	public Class<?>[] getRestEndpointClasses() {
		return this.endpointClasses;
	}

	public void setBindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setRestEndpointClasses(Class<?>[] endpointClasses) {
		this.endpointClasses = endpointClasses;
	}

	public ServerType getServerType() {
		return serverType;
	}

	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}

	@Override
	public Config getRawConfiguration() {
		return this.rawConfiguration;
	}

	public void setRawConfiguration(Config rawConfiguration) {
		this.rawConfiguration = rawConfiguration;
	}

	@Override
	public AchooActorSystem getAchooActorSystem() {
		return this.achooSystem;
	}
	
	public void setAchooActorSystem(AchooActorSystem achooSystem) {
		this.achooSystem = achooSystem;
	}
		
}
