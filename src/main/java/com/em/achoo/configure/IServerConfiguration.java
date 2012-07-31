package com.em.achoo.configure;

import com.em.achoo.server.ServerType;

public interface IServerConfiguration {

	public int getPort();
	
	public String getBindAddress();
	
	public Class<?>[] getRestEndpointClasses();
	
	public ServerType getServerType();
	
}
