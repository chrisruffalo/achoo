package com.em.achoo.server.impl.jetty;

import java.net.URL;

import javax.servlet.ServletContext;

import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.scannotation.ClasspathUrlFinder;
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
	      if(url == null){
	    	  url = ClassLoader.getSystemClassLoader().getResource(".");
	      }
	      // End: RESTeasy-251
	      
	      //additional fix, embedded jetty doesn't "bend" classpath when run in full jar mode
	      //fixes RESTeasy-251 as well
	      if(urls == null || urls.length < 1) {
	    	  urls = ClasspathUrlFinder.findClassPaths();
	      }	      
	      
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
