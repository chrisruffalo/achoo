package com.em.achoo.actors.exchange;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import scala.collection.immutable.VectorBuilder;
import akka.actor.ActorRef;
import akka.actor.Cell;
import akka.actor.Props;
import akka.routing.RoundRobinRouter;
import akka.routing.RoutedActorCell;
import akka.routing.RoutedActorRef;

import com.em.achoo.model.Message;

public class QueueExchange extends AbstractExchange {
	
	private Semaphore routerChangeSemaphore = null;
	
	private RoutedActorRef queueRouter = null;
	
	public QueueExchange(Semaphore routerChangeSemaphore) {
		this.routerChangeSemaphore = routerChangeSemaphore;
	}
	
	@Override
	protected void postSubscribe(ActorRef ref) {
		
		if(this.queueRouter == null) {
			try {
				this.routerChangeSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(this.queueRouter == null) {
				List<ActorRef> subscriberRef = new ArrayList<ActorRef>();
				subscriberRef.add(ref);
				ActorRef createdRouterRef = this.context().actorOf(new Props().withRouter(RoundRobinRouter.create(subscriberRef)));
				if(createdRouterRef instanceof RoutedActorRef) {
					this.queueRouter = (RoutedActorRef)createdRouterRef;
				} else {
					throw new IllegalStateException("Could not create a router reference, created : " + createdRouterRef.getClass().getName());
				}
				
				//return
				return;
			}
			
			this.routerChangeSemaphore.release();
		}
		
		//get the underlying mechanics of the routed actor and add a list of new routees... in this case *one*
		Cell cell = this.queueRouter.underlying();
		if(cell instanceof RoutedActorCell) {
			RoutedActorCell routedCell = (RoutedActorCell)cell;
			VectorBuilder<ActorRef> routeeVectorBuilder = new VectorBuilder<ActorRef>();
			routeeVectorBuilder.$plus$eq(ref);
			routedCell.addRoutees(routeeVectorBuilder.result());
		}
	}

	@Override
	protected ActorRef sendMessage(Message message) {
		if(this.queueRouter == null) {
			return this.queueRouter;
		}
		
		//send message to router
		this.queueRouter.tell(message);		
		
		//return router ref
		return this.queueRouter;
	}

}
