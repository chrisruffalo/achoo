package com.em.achoo.endpoint.test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class Echo {

	private Logger logger = LoggerFactory.getLogger(Echo.class);
	
	@Path("/echo")
	@PUT
	@POST
	@Consumes(value={MediaType.WILDCARD})
	@Produces(value={MediaType.APPLICATION_OCTET_STREAM})
	public void echo(@Context HttpServletRequest request) {
		this.logger.info("GOT ECHO!");				
	}
}
