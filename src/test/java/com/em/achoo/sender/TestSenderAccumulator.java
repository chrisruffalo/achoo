package com.em.achoo.sender;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.em.achoo.model.Message;
import com.em.achoo.model.test.Increment;
import com.em.achoo.model.test.Retrieve;

import akka.transactor.UntypedTransactor;


public class TestSenderAccumulator extends UntypedTransactor {

	private Logger logger = LoggerFactory.getLogger(TestSenderAccumulator.class);
	
	private int accumulator = 0;
	
	private CountDownLatch latch = null;
	
	public TestSenderAccumulator(CountDownLatch latch) {
		this.latch = latch;
	}
	
	@Override
	public void atomically(Object arg0) throws Exception {
		this.logger.trace("Atomic! {}", arg0.getClass().getName());		
		
		if(arg0 instanceof Message) {
			this.logger.trace("Got message: {}", ((Message) arg0).getId());
			this.latch.countDown();
			this.accumulator++;
		} else if(arg0 instanceof Increment) {
			this.latch.countDown();
			this.accumulator++;
		} else if(arg0 instanceof Retrieve) {
			this.sender().tell(new Integer(this.accumulator));
		}
	}
	
}
