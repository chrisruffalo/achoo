package com.em.achoo.model.interfaces;

import com.em.achoo.actors.interfaces.ISender;
import com.em.achoo.model.exchange.ExchangeInformation;

public interface ISubscription {

	public String getId();
	
	public ExchangeInformation getExchangeInformation();
	
	public ISender createSender();
	
}
