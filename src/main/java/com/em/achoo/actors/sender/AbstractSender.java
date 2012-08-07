package com.em.achoo.actors.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.em.achoo.actors.interfaces.ISender;
import com.em.achoo.model.interfaces.ISubscription;

public abstract class AbstractSender implements ISender {

	private ISubscription subscription = null;
	
	private Logger logger = LoggerFactory.getLogger(AbstractSender.class);
	
	protected ISubscription getSubscription() {
		if(this.subscription == null) {
			throw new IllegalStateException("A sender must be initialized with a subscription before being used.");
		}
		return this.subscription;
	}

	@Override
	public void init(ISubscription subscription) {
		this.subscription = subscription;
		
		this.logger.debug("Initialized sender for {} ({})", subscription.getId(), subscription.getClass().getName());
	}

	@Override
	public String getName() {
		return this.getSubscription().getId();
	}
	
	
		
}
