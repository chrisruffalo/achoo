package com.em.achoo.actors.exchange;

import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.TypedActor;
import akka.dispatch.Future;
import akka.dispatch.Futures;
import akka.japi.Creator;

import com.em.achoo.actors.AchooActorSystem;
import com.em.achoo.actors.interfaces.IExchangeManager;
import com.em.achoo.model.Message;
import com.em.achoo.model.Subscription;
import com.em.achoo.model.UnsubscribeMessage;
import com.em.achoo.model.interfaces.IExchange;

public class ExchangeManager implements IExchangeManager {

	private Logger logger = LoggerFactory.getLogger(ExchangeManager.class);
	
	@Override
	public Future<Boolean> dispatch(String exchangeName, Message message) {
		ActorRef dispatchRef = TypedActor.context().actorFor(exchangeName);
		
		if(dispatchRef == null || dispatchRef.isTerminated()) {
			this.logger.info("Could not route message '{}' non-existant exchange '{}'", message.getId(), exchangeName);
			return Futures.successful(false, TypedActor.context().dispatcher());	
		}
		
		//send message to exchange (topic or queue) for further dispatch
		dispatchRef.tell(message);

		//return response
		return Futures.successful(true, TypedActor.context().dispatcher());
	}

	@Override
	public Subscription subscribe(Subscription subscription) {
		
		IExchange exchange = subscription.getExchange();
		
		ActorRef dispatchRef = TypedActor.context().actorFor(exchange.getName());
		if(dispatchRef == null || dispatchRef.isTerminated()) {
			switch(exchange.getType()) {
				case TOPIC:
					dispatchRef = TypedActor.context().actorOf(new Props(TopicExchange.class), exchange.getName());
					break;
				case QUEUE:
					dispatchRef = TypedActor.context().actorOf(new Props(QueueExchange.class), exchange.getName());
					break;
			}
			this.logger.info("Created exchange: {} of type {}", exchange.getName(), exchange.getType());
		}
		
		//update subscription
		subscription.setId(UUID.randomUUID().toString().toUpperCase());
		
		//tell exchange to support subscriber
		dispatchRef.tell(subscription);
		
		return subscription;
	}
	
	@Override
	public boolean unsubscribe(String exchangeName, String subscriptionId) {
		ActorRef exchangeRef = TypedActor.context().actorFor(exchangeName);
		//if there is an actor for the given exchange, notify it that it should shut down that subscription
		if(exchangeRef != null && !exchangeRef.isTerminated()) {
			UnsubscribeMessage message = new UnsubscribeMessage();
			message.setSubscriptionId(subscriptionId);
			exchangeRef.tell(message);
		}		
		return true;
	}
	
	private volatile static IExchangeManager instance = null;
	
	private static Semaphore managerLockSemaphore = new Semaphore(1, true);
	
	public static IExchangeManager get() {

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
		IExchangeManager emActor = AchooActorSystem.INSTANCE.getTypedActor(IExchangeManager.class, creator, "achoo-exchange-manager");
		
		//save instance of created manager
		ExchangeManager.instance = emActor;
		
		//release semaphore
		ExchangeManager.managerLockSemaphore.release();
		
		//return
		return emActor;
	}
	
}
