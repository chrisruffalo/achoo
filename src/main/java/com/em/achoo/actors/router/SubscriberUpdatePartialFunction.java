package com.em.achoo.actors.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.collection.immutable.VectorBuilder;
import scala.runtime.AbstractPartialFunction;
import scala.runtime.BoxedUnit;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.routing.RoutedActorCell;
import akka.routing.Router;

import com.em.achoo.actors.exchange.ExchangeManager;
import com.em.achoo.actors.sender.factory.SubscriptionSenderFactory;
import com.em.achoo.model.subscription.Subscription;


public class SubscriberUpdatePartialFunction extends AbstractPartialFunction<Object, BoxedUnit> {

	private Logger logger = LoggerFactory.getLogger(SubscriberUpdatePartialFunction.class);
	

	private Router router = null;
	
	public SubscriberUpdatePartialFunction(Router router) {
		this.router = router;
	}
	
	@Override
	public boolean isDefinedAt(Object arg0) {
		this.logger.debug("Checking defined for: {}", arg0.getClass().getName());
		if(arg0 instanceof Subscription || arg0 instanceof Terminated) {
			return true;
		}		
		return false;
	}

	@Override
	public BoxedUnit apply(Object x) {
		this.logger.debug("Apply! ...: {}", x.getClass().getName());		

		BoxedUnit response = null;
		
		if(this.router != null) {
			
			ActorContext context = this.router.context();
			if(context instanceof RoutedActorCell) {
				RoutedActorCell routedCell = (RoutedActorCell)context;
			
				VectorBuilder<ActorRef> routeeVectorBuilder = new VectorBuilder<ActorRef>();

				if(x instanceof Subscription) {
					Subscription sub = (Subscription) x;
					
					String subName = ExchangeManager.SUBSCRIPTION_PREFIX + sub.getId();
					
					SubscriptionSenderFactory factory = new SubscriptionSenderFactory(sub);
					Props senderProps = new Props(factory);
					ActorRef subscriber = context.actorOf(senderProps, subName);
					
					routeeVectorBuilder.$plus$eq(subscriber);
					routedCell.addRoutees(routeeVectorBuilder.result());
				} else if(x instanceof Terminated) {
					Terminated terminated = (Terminated)x;
					ActorRef terminatedActor = terminated._1();
					
					routeeVectorBuilder.$plus$eq(terminatedActor);
					routedCell.removeRoutees(routeeVectorBuilder.result());		
				} else {
					this.logger.warn("No modifications made");
				}
			}				
		}
		
		return response;
	}
	
	
	

}
