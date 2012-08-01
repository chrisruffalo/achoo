package com.em.achoo.server.impl;

import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;

import com.em.achoo.configure.IServerConfiguration;
import com.em.achoo.server.IServer;
import com.em.achoo.server.impl.jetty.AchooResteasyBootstrap;

public class JettyServerImpl implements IServer {

	private Server jettyServer = null;
	
	@Override
	public void start(IServerConfiguration configuration) {
		if(this.jettyServer != null) {
			return;
		}
		
		InetSocketAddress address = new InetSocketAddress(configuration.getBindAddress(), configuration.getPort());
		this.jettyServer = new Server(address);
		
		//create handler
		ServletContextHandler restServletHandler = new ServletContextHandler();
		
		//set scanner to true
		restServletHandler.getInitParams().put(ResteasyContextParameters.RESTEASY_SCAN, "true");
		//use built-in providers as well		
		restServletHandler.getInitParams().put(ResteasyContextParameters.RESTEASY_USE_BUILTIN_PROVIDERS, "true");
		
		AchooResteasyBootstrap bootstrap = new AchooResteasyBootstrap();
		restServletHandler.addEventListener(bootstrap);

		//create servlet dispatcher for rest endpoints that will be automatically found by the bootstrap
		restServletHandler.addServlet(HttpServletDispatcher.class, "/");
		
		//set handler
		this.jettyServer.setHandler(restServletHandler);
		
		//start the jetty server
		try {
			this.jettyServer.start();
		} catch (Exception e) {
			this.jettyServer = null;
		}		
	}

	@Override
	public void stop() {
		if(this.jettyServer != null) {
			this.jettyServer.setGracefulShutdown(5000);
			try {
				this.jettyServer.stop();
			} catch (Exception e) {
				
			}
		}
	}

}
