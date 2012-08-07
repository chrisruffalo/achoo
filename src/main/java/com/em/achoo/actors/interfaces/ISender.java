package com.em.achoo.actors.interfaces;

import com.em.achoo.model.Message;
import com.em.achoo.model.interfaces.ISubscription;

public interface ISender {

	public void send(Message message);
	
	public void init(ISubscription subscription);
	
	public String getName();
	
}
