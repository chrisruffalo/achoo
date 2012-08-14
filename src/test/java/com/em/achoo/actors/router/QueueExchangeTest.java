package com.em.achoo.actors.router;

import java.util.UUID;

import com.em.achoo.model.exchange.ExchangeInformation;
import com.em.achoo.model.exchange.ExchangeType;

public class QueueExchangeTest extends AbstractExchangeTest {

	@Override
	protected ExchangeInformation createTestExchange() {
		ExchangeInformation exchange = new ExchangeInformation();
		
		exchange.setName(UUID.randomUUID().toString().toUpperCase());
		exchange.setType(ExchangeType.QUEUE);
		
		return exchange;
	}

	@Override
	protected int calculateExpectedResponses(int subscribers) {
		return 1;
	}

}
