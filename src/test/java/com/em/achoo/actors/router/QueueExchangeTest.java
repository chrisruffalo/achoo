package com.em.achoo.actors.router;

import java.util.UUID;

import com.em.achoo.model.exchange.Exchange;
import com.em.achoo.model.exchange.ExchangeType;

public class QueueExchangeTest extends AbstractExchangeTest {

	@Override
	protected Exchange createTestExchange() {
		Exchange exchange = new Exchange();
		
		exchange.setName(UUID.randomUUID().toString().toUpperCase());
		exchange.setType(ExchangeType.QUEUE);
		
		return exchange;
	}

	@Override
	protected int calculateExpectedResponses(int subscribers) {
		return 1;
	}

}
