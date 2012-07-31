package com.em.achoo.server;

public enum ServerType {
	JETTY,
	TJWS
	;
	
	public static ServerType getServerForString(String serverType) {
		//return null server if we know the parse won't work
		if(serverType == null || serverType.isEmpty()) {
			return null;
		}
		
		//uppercase
		serverType = serverType.toUpperCase();
		
		//server type value of
		ServerType type = null;
		try {
			type = ServerType.valueOf(serverType);
		} catch(EnumConstantNotPresentException ex) {
			//netty as default server
			type = ServerType.JETTY;
		}
		return type;
	}
}
