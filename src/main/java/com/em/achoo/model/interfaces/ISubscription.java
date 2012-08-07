package com.em.achoo.model.interfaces;

import com.em.achoo.actors.interfaces.ISender;

public interface ISubscription {

	public String getId();
	
	public IExchange getExchange();
	
	public ISender createSender();
	
}
