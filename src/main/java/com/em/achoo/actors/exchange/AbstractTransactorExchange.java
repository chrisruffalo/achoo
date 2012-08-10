package com.em.achoo.actors.exchange;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.transactor.UntypedTransactor;

import com.em.achoo.model.Envelope;
import com.em.achoo.model.Message;
import com.em.achoo.model.management.UnsubscribeMessage;
import com.em.achoo.model.subscription.Subscription;

public abstract class AbstractTransactorExchange extends UntypedTransactor {

	private Logger logger = LoggerFactory.getLogger(AbstractTransactorExchange.class);
	
	protected abstract Collection<Subscription> getRecipientsForMessage(Message message);
	
	protected abstract Collection<Subscription> getSubscribers();
	
	protected abstract void addSubscriber(Subscription subscription);
	protected abstract void removeSubscriber(UnsubscribeMessage unsubscribe);	
	
	@Override
	public void atomically(Object arg0) throws Exception {
		if(arg0 instanceof Subscription) {
			this.addSubscriber((Subscription)arg0);
		} else if(arg0 instanceof UnsubscribeMessage) {
			this.removeSubscriber((UnsubscribeMessage)arg0);
		}
	}

	@Override
	public boolean normally(Object message) throws Exception {
		if(message instanceof Message) {
			//collect recipients for the message			
			Collection<Subscription> recipients = this.getRecipientsForMessage((Message) message);
			
			this.logger.trace("Telling {} about mailbag with {} recipients (message {})", new Object[]{this.sender().path().toString(), recipients.size(), ((Message) message).getId()});

			//get sender pool reference
			ActorRef senderPool = this.context().actorFor("/user/senders");
			if(senderPool == null || senderPool.isTerminated()) {
				if(senderPool == null) {
					this.logger.warn("No sender pool available at '/user/senders'.");
				} else {
					this.logger.warn("No sender pool available at '{}'", senderPool.path().toString());
				}
				//end of handler chain, even with error
				return true;
			}			
			
			for(Subscription subscription : recipients) {
			Envelope e = new Envelope(subscription, (Message)message);
				//reply to the sender with the bag
				senderPool.tell(e);
			}			

			//return that it was handled normally
			return true;
		} 
		return false;
	}
	
	@Override
	public void postStop() {
		for(Subscription sub : this.getSubscribers()) {
			this.self().tell(sub);
		}
	}
	
}
