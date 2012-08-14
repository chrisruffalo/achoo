package com.em.achoo.actors.exchange.storage.impl.memory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.em.achoo.actors.exchange.storage.IExchange;
import com.em.achoo.actors.exchange.storage.IExchangeStorage;
import com.em.achoo.model.exchange.ExchangeInformation;
import com.em.achoo.model.subscription.Subscription;

public class InMemoryExchangeStorage implements IExchangeStorage {

	private Logger logger = LoggerFactory.getLogger(InMemoryExchangeStorage.class);
	
	private Map<ExchangeInformation, IExchange> exchangeMap = new ConcurrentHashMap<ExchangeInformation, IExchange>();
	
	@Override
	public void add(Subscription subscription) {
		ExchangeInformation info = subscription.getExchangeInformation();
		
		IExchange exchange = this.exchangeMap.get(info);
		if(exchange == null) {
			exchange = this.createExchange(subscription);
			this.exchangeMap.put(info, exchange);
		}
		exchange.addSubscriber(subscription);

	}

	private IExchange createExchange(Subscription subscription) {
		ExchangeInformation info = subscription.getExchangeInformation();
		
		IExchange exchange = null;
		switch(info.getType()) {
			case QUEUE:
				exchange = new InMemoryQueue(info);
				break;
			case TOPIC:
				exchange = new InMemoryTopic(info);
				break;
		}
		
		this.logger.info("Created {}", exchange.getExchangeInformation().toString());
		
		return exchange;
	}

	@Override
	public void remove(Subscription subscription) {
		ExchangeInformation info = subscription.getExchangeInformation();
		
		IExchange exchange = this.exchangeMap.get(info);
		if(exchange != null) {
			exchange.removeSubscriber(subscription);
		}
	}

	@Override
	public Collection<Subscription> getNextSubscribers(ExchangeInformation info) {

		IExchange exchange = this.exchangeMap.get(info);
		if(exchange == null) {
			return Collections.emptyList();
		}
		
		return exchange.getNextSubscribers();
	}

}
