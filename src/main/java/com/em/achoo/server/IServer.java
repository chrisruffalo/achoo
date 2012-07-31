package com.em.achoo.server;

import com.em.achoo.configure.IServerConfiguration;

public interface IServer {

	public void start(IServerConfiguration configuration);
	
	public void stop();
	
}
