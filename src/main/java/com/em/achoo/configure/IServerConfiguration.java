package com.em.achoo.configure;

import com.em.achoo.actors.AchooActorSystem;
import com.em.achoo.server.ServerType;
import com.typesafe.config.Config;

public interface IServerConfiguration {

	public int getPort();
	
	public String getBindAddress();
	
	public Class<?>[] getRestEndpointClasses();
	
	public ServerType getServerType();
	
	public Config getRawConfiguration();
	
	public AchooActorSystem getAchooActorSystem();
	
}
