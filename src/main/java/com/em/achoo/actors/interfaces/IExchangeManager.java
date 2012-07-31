package com.em.achoo.actors.interfaces;

import akka.dispatch.Future;

import com.em.achoo.model.Message;
import com.em.achoo.model.Subscription;

public interface IExchangeManager {

	public Future<Boolean> dispatch(String exchangeName, Message message);
	
	public Subscription subscribe(Subscription subscription);
	
	public boolean unsubscribe(String exchangeName, String subscriptionId);
	
}
