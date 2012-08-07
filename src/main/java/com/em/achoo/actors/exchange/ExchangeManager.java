package com.em.achoo.actors.exchange;

import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.Future;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.TypedActor;
import akka.dispatch.Futures;
import akka.japi.Creator;
import akka.routing.BroadcastRouter;
import akka.routing.RoundRobinRouter;
import akka.routing.RouterConfig;

import com.em.achoo.actors.AchooActorSystem;
import com.em.achoo.actors.interfaces.IExchangeManager;
import com.em.achoo.actors.router.AutomaticDynamicRouterConfig;
import com.em.achoo.actors.router.DynamicResizer;
import com.em.achoo.model.Message;
import com.em.achoo.model.Subscription;
import com.em.achoo.model.UnsubscribeMessage;
import com.em.achoo.model.interfaces.IExchange;

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
			
			//create subscription name
			
			//create routee list
			//List<ActorRef> routees = new ArrayList<ActorRef>(1);
			
		
			//add to 
			
			switch(exchange.getType()) {
				case TOPIC:
					//dispatchRef = TypedActor.context().actorOf(new Props(new TopicExchangeRoutingFactory()), exchange.getName());
					baseRouterConfig = new BroadcastRouter(new DynamicResizer());
					break;
				case QUEUE:
					//dispatchRef = TypedActor.context().actorOf(new Props(new QueueExchangeRoutingFactory()), exchange.getName());
					baseRouterConfig = new RoundRobinRouter(new DynamicResizer());
					break;
			}
			dispatchRef = TypedActor.context().actorOf(new Props().withRouter(new AutomaticDynamicRouterConfig(baseRouterConfig)), exchange.getName());
			
			this.logger.info("Created exchange: {} of type {} (at path: {})", new Object[]{exchange.getName(), exchange.getType(), dispatchRef.path().toString()});
		}

		//tell exchange to support subscriber
		dispatchRef.tell(subscription);
		
		return subscription;
	}
	
	@Override
	public boolean unsubscribe(String exchangeName, String subscriptionId) {
		ActorRef subscriptionRef = TypedActor.context().actorFor(exchangeName + "/" + ExchangeManager.SUBSCRIPTION_PREFIX + subscriptionId);
		//if there is an actor for the given subscription, notify it to shutdown
		if(subscriptionRef != null && !subscriptionRef.isTerminated()) {
			UnsubscribeMessage message = new UnsubscribeMessage();
			message.setSubscriptionId(subscriptionId);
			subscriptionRef.tell(message);
		}		
		return true;
	}
	
	private volatile static IExchangeManager instance = null;
	
	private static Semaphore managerLockSemaphore = new Semaphore(1, true);
	
	public static IExchangeManager get(ActorSystem system) {

		//simple optimization, don't get lock unless you need it by 
		//returing the instance early
		if(ExchangeManager.instance != null) {
			return ExchangeManager.instance;
		}
		
		//otherwise acquire a lock
		try {
			ExchangeManager.managerLockSemaphore.acquire();
		} catch (InterruptedException e) {
			return null;
		}
		
		//check again in case the status changed while you had a lock
		if(ExchangeManager.instance != null) {
			return ExchangeManager.instance;
		}
		
		//create an exchange manager creator that re-uses the same base instance
		//(becomes sort of an actor based pojo-backed singleton)
		final ExchangeManager manager = new ExchangeManager();
		
		//create creator
		Creator<ExchangeManager> creator = new Creator<ExchangeManager>() {

			@Override
			public ExchangeManager create() {

				return manager;
			}
		};
		
		//use the creator to create our exchange manager isntance
		IExchangeManager emActor = AchooActorSystem.getTypedActor(system, IExchangeManager.class, creator, "achoo-exchange-manager");
		
		//save instance of created manager
		ExchangeManager.instance = emActor;
		
		//release semaphore
		ExchangeManager.managerLockSemaphore.release();
		
		//return
		return emActor;
	}
	
}
