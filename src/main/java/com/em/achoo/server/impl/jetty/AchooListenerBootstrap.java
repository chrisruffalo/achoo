package com.em.achoo.server.impl.jetty;

import java.net.URL;

import javax.servlet.ServletContext;

import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.scannotation.WarUrlFinder;

public class AchooListenerBootstrap extends ListenerBootstrap {

	public AchooListenerBootstrap(ServletContext servletContext) {
		super(servletContext);
	}

	public URL[] getScanningUrls()
	   {
	      URL[] urls = findWebInfLibClasspaths(servletContext);
	      URL url = WarUrlFinder.findWebInfClassesPath(servletContext);
	      
	      // Matthew Gilliard fix RESTeasy-251
	      if (url == null){
	    	  url = this.getClass().getClassLoader().getResource(".");
	      }
	      // End: RESTeasy-251
	      
	      if (url == null) return urls;
	      URL[] all = new URL[urls.length + 1];
	      int i = 0;
	      for (i = 0; i < urls.length; i++)
	      {
	         all[i] = urls[i];
	      }
	      all[i] = url;
	      return all;
	   }
	
}
