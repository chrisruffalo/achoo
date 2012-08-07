package com.em.achoo.endpoint.management;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import com.em.achoo.actors.AchooManager;
import com.em.achoo.endpoint.AbstractEndpoint;
import com.em.achoo.model.management.StopMessage;

@Path("/management")
public class ManagementEndpoint extends AbstractEndpoint {
	
	@Path("/kill")
	@GET
	@PUT
	@POST
	public String kill() {
		
		ActorSystem system = this.getActorSystem();
		
		ActorRef ref = system.actorFor("/user/" + AchooManager.NAME);
		
		//kill system
		ref.tell(new StopMessage());
		
		return "killed";
	}
}
