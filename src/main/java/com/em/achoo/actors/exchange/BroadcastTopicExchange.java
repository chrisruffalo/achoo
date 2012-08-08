package com.em.achoo.actors.exchange;

import java.util.Collection;
import java.util.Collections;

import akka.agent.Agent;

import com.em.achoo.model.Message;
import com.em.achoo.model.subscription.Subscription;

public class BroadcastTopicExchange extends AbstractExchange {

	public BroadcastTopicExchange(Agent<Collection<Subscription>> subscribers) {
		super(subscribers);
	}

	@Override
	protected Collection<Subscription> getRecipientsForMessage(Message message) {
		return Collections.unmodifiableCollection(this.getSubscribers());
	}
	
}
