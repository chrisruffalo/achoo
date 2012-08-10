package com.em.achoo.sender;

import java.util.concurrent.CountDownLatch;

import akka.actor.Actor;
import akka.actor.UntypedActorFactory;

public class TestSenderAccumulatorFactory implements UntypedActorFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CountDownLatch latch = null;
	
	public TestSenderAccumulatorFactory(CountDownLatch latch) {
		this.latch = latch;
	}
	
	@Override
	public Actor create() {
		return new TestSenderAccumulator(this.latch);
	}

}
