package com.em.achoo.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class FavoriteIcon {

	@Path("favicon.ico")
	@GET
	@Consumes(value={MediaType.WILDCARD})
	@Produces(value={MediaType.APPLICATION_OCTET_STREAM})
	public byte[] getFavoriteIcon() {
		return new byte[0];
	}
	
}
