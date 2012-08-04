package com.em.achoo.actors.exchange;

import java.util.List;
import java.util.UUID;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.agent.Agent;
import akka.routing.RoundRobinRouter;
import akka.util.Timeout;

import com.em.achoo.model.Message;

public class QueueExchange extends AbstractExchange {
	
	private Agent<String> routerIdAgent = null;
	
	public QueueExchange(Agent<String> routerIdAgent) {
		this.routerIdAgent = routerIdAgent;
	}
	
	@Override
	protected void postSubscribe(ActorRef ref) {
		//get old router id to stop the router
		String oldRouterId = this.routerIdAgent.await(Timeout.intToTimeout(100));
		//manufacture new router id
		String newRouterId = UUID.randomUUID().toString().toUpperCase();
		
		//create new router
		this.createNewRouter(oldRouterId, newRouterId);
	}

	@Override
	protected ActorRef sendMessage(Message message) {
		//get router unique id
		String routerId = this.routerIdAgent.await(Timeout.intToTimeout(100));
		
		//get routed ref
		ActorRef router = this.context().actorFor(AbstractExchange.ROUTER_PREFIX + routerId);

		//send message to router
		router.tell(message);
		
		return router;
	}
	
	/**
	 * Swaps the router from the old router to the new router and kills the old router
	 * 
	 * @param oldRouterId
	 * @param newRouterId
	 */
	private void createNewRouter(String oldRouterId, String newRouterId) {
		//list of actor refs
		List<ActorRef> routedRefs = this.getSubscribingChildren();
		
		//create the new router
		this.context().actorOf(new Props().withRouter(RoundRobinRouter.create(routedRefs)), AbstractExchange.ROUTER_PREFIX + newRouterId);

		//send the update to the agent so that future messages 
		this.routerIdAgent.send(newRouterId);
		
		//make sure an old id exists:
		if(oldRouterId != null) {
			//lastly, stop the old router (may need to change this to poison pill?)
			ActorRef router = this.context().actorFor(QueueExchange.ROUTER_PREFIX + oldRouterId);
			if(router != null && !router.isTerminated()) {
				//kill router
				this.context().stop(router);					
			}
		}
	}


}
