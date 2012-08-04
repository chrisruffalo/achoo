package com.em.achoo.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * If you hit this with a browser, you might want an icon returned, in which 
 * case an icon will be returned (application/octet-stream) by the browser 
 * 
 * @author chris
 *
 */
@Path("/")
public class FavoriteIcon {

	/**
	 * Returns an icon as a byte array.
	 * 
	 * @return
	 */
	@Path("favicon.ico")
	@GET
	@Consumes(value={MediaType.WILDCARD})
	@Produces(value={MediaType.APPLICATION_OCTET_STREAM})
	public byte[] getFavoriteIcon() {
		return new byte[0];
	}
	
}
