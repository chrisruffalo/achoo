package com.em.achoo.server;

import com.em.achoo.server.impl.JettyServerImpl;

public class ServerFactory {
	
	private ServerFactory() {
		
	}
	
	public static IServer getServer(ServerType type) {
		
		IServer server = null;
		
		switch(type) {
			case JETTY:
				server = new JettyServerImpl();
				break;
		}
		
		return server;
	}
	
}
