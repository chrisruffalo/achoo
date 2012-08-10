package com.em.achoo.actors.exchange;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.em.achoo.model.Message;
import com.em.achoo.model.management.UnsubscribeMessage;
import com.em.achoo.model.subscription.NoOpSubscription;
import com.em.achoo.model.subscription.Subscription;

public class BroadcastTopicTransactorExchange extends AbstractTransactorExchange {

	private Set<Subscription> subscribers = null;
	
	public BroadcastTopicTransactorExchange() {
		this.subscribers = new HashSet<Subscription>();
	}
	
	@Override
	protected Collection<Subscription> getRecipientsForMessage(Message message) {
		return Collections.unmodifiableSet(this.subscribers);
	}

	@Override
	protected void addSubscriber(Subscription subscription) {
		this.subscribers.add(subscription);
	}

	@Override
	protected void removeSubscriber(UnsubscribeMessage unsubscribe) {
		//create dummy subscription impl
		Subscription dummy = new NoOpSubscription();
		dummy.setId(unsubscribe.getSubscriptionId());
		this.subscribers.remove(dummy);
	}

	@Override
	protected Collection<Subscription> getSubscribers() {
		return Collections.unmodifiableCollection(this.subscribers);
	}

}
