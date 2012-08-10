package com.em.achoo.server.impl;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;

import com.em.achoo.configure.ConfigurationKeys;
import com.em.achoo.configure.IServerConfiguration;
import com.em.achoo.server.IServer;
import com.em.achoo.server.impl.jetty.AchooResteasyBootstrap;
import com.google.common.base.Joiner;

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
		
		//set scan for providers
		restServletHandler.getInitParams().put(ResteasyContextParameters.RESTEASY_SCAN_PROVIDERS, "true");
		//but we'll provide resources through configuration
		restServletHandler.getInitParams().put(ResteasyContextParameters.RESTEASY_SCAN_RESOURCES, "false");
		//use built-in providers as well		
		restServletHandler.getInitParams().put(ResteasyContextParameters.RESTEASY_USE_BUILTIN_PROVIDERS, "true");
				
		//resources
		if(configuration.getRestEndpointClasses().length > 0) {
			//for each endpoint, get full class name for resource loader
			Set<String> endpointNames = new HashSet<String>(configuration.getRestEndpointClasses().length);
			for(Class<?> endpoint : configuration.getRestEndpointClasses()) {
				endpointNames.add(endpoint.getName());
			}
			
			//join names into comma sepereated list
			Joiner joiner = Joiner.on(",").skipNulls();
			String joinedEndpointNames = joiner.join(endpointNames);
			
			//set in parameterfor ResteasyBootstrap
			restServletHandler.getInitParams().put(ResteasyContextParameters.RESTEASY_RESOURCES, joinedEndpointNames);	
		}
		
		
		AchooResteasyBootstrap bootstrap = new AchooResteasyBootstrap();
		restServletHandler.addEventListener(bootstrap);

		//create servlet dispatcher for rest endpoints that will be automatically found by the bootstrap
		restServletHandler.addServlet(HttpServletDispatcher.class, "/");
		
		//set attributes for contained items
		restServletHandler.getServletContext().setAttribute(ConfigurationKeys.ACHOO_CONFIG.toString(), configuration.getRawConfiguration());
		restServletHandler.getServletContext().setAttribute(ConfigurationKeys.ACHOO_REFERENCE.toString(), configuration.getAchooReference());
		
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
