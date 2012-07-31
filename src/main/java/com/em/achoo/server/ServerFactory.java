package com.em.achoo.server;

import com.em.achoo.server.impl.JettyServerImpl;
import com.em.achoo.server.impl.TJWServerImpl;

public class ServerFactory {
	
	private ServerFactory() {
		
	}
	
	public static IServer getServer(ServerType type) {
		
		IServer server = null;
		
		switch(type) {
			case JETTY:
				server = new JettyServerImpl();
				break;
			case TJWS: 
				server = new TJWServerImpl();
				break;
		}
		
		return server;
	}
	
}
