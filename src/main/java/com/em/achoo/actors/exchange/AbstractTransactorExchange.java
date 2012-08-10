package com.em.achoo.actors.exchange;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.transactor.UntypedTransactor;

import com.em.achoo.model.MailBag;
import com.em.achoo.model.Message;
import com.em.achoo.model.management.UnsubscribeMessage;
import com.em.achoo.model.subscription.Subscription;

public abstract class AbstractTransactorExchange extends UntypedTransactor {

	private Logger logger = LoggerFactory.getLogger(AbstractTransactorExchange.class);
	
	protected abstract Collection<Subscription> getRecipientsForMessage(Message message);
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
			
			//put them in a mailbag
			MailBag bag = new MailBag();
			bag.setSubscriptions(recipients);
			bag.setMessage((Message) message);
			
			this.logger.trace("Telling {} about mailbag with {} recipients (message {})", new Object[]{this.sender().path().toString(), bag.getSubscriptions().size(), bag.getMessage().getId()});
			
			//reply to the sender with the bag
			this.sender().tell(bag);
			
			//return that it was handled normally
			return true;
		} 
		return false;
	}
	
}
