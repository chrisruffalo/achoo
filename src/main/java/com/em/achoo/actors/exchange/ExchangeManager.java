package com.em.achoo.actors.exchange;

import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.Future;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.TypedActor;
import akka.agent.Agent;
import akka.dispatch.Futures;
import akka.routing.BroadcastRouter;
import akka.routing.RoundRobinRouter;
import akka.routing.RouterConfig;

import com.em.achoo.actors.AchooActorSystem;
import com.em.achoo.actors.interfaces.IExchangeManager;
import com.em.achoo.actors.router.AutomaticDynamicRouterConfig;
import com.em.achoo.actors.router.DynamicResizer;
import com.em.achoo.model.Message;
import com.em.achoo.model.interfaces.IExchange;
import com.em.achoo.model.subscription.Subscription;

/**
 * Manages exchanges by routing subscription, unsubscribe, and message dispatches to implementations of the
 * various exchange types, who forward it to subscription types.
 * 
 * @author chris
 *
 */
public class ExchangeManager implements IExchangeManager {

	public static final String SUBSCRIPTION_PREFIX = "subscription-";
	
	private Logger logger = LoggerFactory.getLogger(ExchangeManager.class);
	
	@Override
	public Future<Boolean> dispatch(String exchangeName, Message message) {
		ActorRef dispatchRef = TypedActor.context().actorFor(exchangeName);
		
		if(dispatchRef == null || dispatchRef.isTerminated()) {
			this.logger.info("Could not route message '{}' non-existant exchange '{}'", message.getId(), exchangeName);
			
			return Futures.successful(false);	
		}
		
		//send message to exchange (topic or queue) for further dispatch
		dispatchRef.tell(message);
		
		//return response
		return Futures.successful(true);
	}

	@Override
	public Subscription subscribe(Subscription subscription) {
		
		IExchange exchange = subscription.getExchange();

		//update subscription
		subscription.setId(UUID.randomUUID().toString().toUpperCase());
		
		ActorRef dispatchRef = TypedActor.context().actorFor(exchange.getName());
		if(dispatchRef == null || dispatchRef.isTerminated()) {
			RouterConfig baseRouterConfig = null;
			
			//create router based on type  			
			switch(exchange.getType()) {
				case TOPIC:
					baseRouterConfig = new BroadcastRouter(new DynamicResizer());
					break;
				case QUEUE:
					baseRouterConfig = new RoundRobinRouter(new DynamicResizer());
					break;
			}
			dispatchRef = TypedActor.context().actorOf(new Props().withRouter(new AutomaticDynamicRouterConfig(baseRouterConfig)), exchange.getName());
			
			this.logger.info("Created exchange: {} of type {} (at path: {})", new Object[]{exchange.getName(), exchange.getType(), dispatchRef.path().toString()});
		}

		//tell exchange to support given subscriber subscriber
		dispatchRef.tell(subscription);
		
		return subscription;
	}
	
	@Override
	public boolean unsubscribe(String exchangeName, String subscriptionId) {
		ActorRef subscriptionRef = TypedActor.context().actorFor(exchangeName + "/" + ExchangeManager.SUBSCRIPTION_PREFIX + subscriptionId);
		//if there is an actor for the given subscription, notify it to shutdown
		if(subscriptionRef != null && !subscriptionRef.isTerminated()) {
			subscriptionRef.tell(PoisonPill.getInstance());
			return true;
		}		
		return false;
	}
	
	private static Agent<IExchangeManager> manager = null;
	
	private static final String ACHOO_EXCHANGE_MANAGER_NAME = "exchange-manager";
	
	private static Semaphore managerLock = new Semaphore(1, true);
	
	public static IExchangeManager get(ActorSystem system) {

		if(manager == null) {
			try {
				managerLock.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(manager == null) {
				IExchangeManager emActor = AchooActorSystem.getTypedActor(system, IExchangeManager.class, ExchangeManager.class, ExchangeManager.ACHOO_EXCHANGE_MANAGER_NAME);
				manager = new Agent<IExchangeManager>(emActor, system);			
			}
			
			managerLock.release();
		}		
	
		//return
		return manager.get();
	}
	
}
