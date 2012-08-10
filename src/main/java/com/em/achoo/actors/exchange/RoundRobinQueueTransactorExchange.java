package com.em.achoo.actors.exchange;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.em.achoo.model.Message;
import com.em.achoo.model.management.UnsubscribeMessage;
import com.em.achoo.model.subscription.NoOpSubscription;
import com.em.achoo.model.subscription.Subscription;

public class RoundRobinQueueTransactorExchange  extends AbstractTransactorExchange {

	//private Logger logger = LoggerFactory.getLogger(AbstractTransactorExchange.class);
	
	private List<Subscription> subscribers = null;
	private int subscriberIndex = 0;
	
	public RoundRobinQueueTransactorExchange() {
		this.subscribers = new LinkedList<Subscription>();
	}
	
	@Override
	protected Collection<Subscription> getRecipientsForMessage(Message message) {
		int modIndex = this.subscriberIndex % this.subscribers.size();
		Subscription subscriber = this.subscribers.get(modIndex);
		this.subscriberIndex++;
		return Collections.singleton(subscriber);
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
		//reduce index so that one doesn't get skipped over
		this.subscriberIndex--;
	}
	
	@Override
	protected Collection<Subscription> getSubscribers() {
		return Collections.unmodifiableCollection(this.subscribers);
	}

}
