package com.em.achoo.actors.interfaces;

import akka.dispatch.Future;

import com.em.achoo.model.Message;



public interface IMessageDispatcher {

	public Future<Boolean> dispatch(Message message);
	
}
