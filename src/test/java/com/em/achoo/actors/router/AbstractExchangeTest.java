package com.em.achoo.actors.router;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.junit.Test;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.util.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;

import com.em.achoo.AchooBaseTest;
import com.em.achoo.model.Message;
import com.em.achoo.model.exchange.Exchange;
import com.em.achoo.model.subscription.Subscription;
import com.em.achoo.model.subscription.TestSubscription;
import com.em.achoo.model.test.Retrieve;
import com.em.achoo.sender.TestSenderAccumulatorFactory;

public abstract class AbstractExchangeTest extends AchooBaseTest {

	private static final int MESSAGE_COUNT = 5000;
	
	protected abstract Exchange createTestExchange();
	protected abstract int calculateExpectedResponses(int subscribers);
	
	@Test
	public void test1Subscriber() {
		this.testXSubscribers(1);
	}
	
	@Test
	public void test50Subscribers() {
		this.testXSubscribers(50);
	}
	
	@Test 
	public void test200Subscribers() {
		this.testXSubscribers(200);
	}
	
	private void testXSubscribers(int subscribers) {
		int expectedResponses = this.calculateExpectedResponses(subscribers) * AbstractExchangeTest.MESSAGE_COUNT;
		CountDownLatch latch = new CountDownLatch(expectedResponses);
		ActorRef accumulator = this.accumulatorRef(latch);
		
		Exchange exchange = this.createTestExchange();
		
		//create subscribers
		for(int i = 0; i < subscribers; i++) {
			Subscription subscription = new TestSubscription(accumulator);
			subscription.setExchange(exchange);
			this.subscribe(subscription);
		}
		
		//send messages
		for(int j = 0; j < AbstractExchangeTest.MESSAGE_COUNT; j++) {
			this.getExchangeRef().tell(Message.create(exchange));
		}
		
		//wait for latch
		try {
			latch.await();
		} catch (InterruptedException e) {
			Assert.fail("Could not wait for latch to complete");
		}
		
		int sent = this.getAccumulated(accumulator);
		
		//assert
		Assert.assertEquals(expectedResponses, sent);		
	}
	
	private void subscribe(Subscription subscription) {
		//subscribe (with one subscriber)
		Future<Object> subscriptionFuture = Patterns.ask(this.getExchangeRef(), subscription, Timeout.intToTimeout(2000));
		try {
			Object result = Await.result(subscriptionFuture, Duration.parse("2 seconds"));
			if(result instanceof Subscription) {
				Subscription sub = (Subscription)result;
				Assert.assertNotNull(sub);
				Assert.assertNotNull(sub.getId());
				Assert.assertFalse(sub.getId().isEmpty());
				//log?
			} else {
				Assert.fail("Returned object was null or not of type Subscription.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Could not await result: " + e.getMessage());			
		}
	}
	
	
	protected ActorRef getExchangeRef() {
		ActorRef ref = this.getAchoo().getExchangeManagerRef();		
		
		return ref;
	}	
	
	protected ActorRef accumulatorRef(CountDownLatch latch) {
		String refName = "accumulator-" + UUID.randomUUID().toString().toUpperCase();
		ActorRef accumulator = this.getAchoo().getAchooActorSystem().getSystem().actorFor(refName);
		if(accumulator == null || accumulator.isTerminated()) {
			accumulator = this.getAchoo().getAchooActorSystem().getSystem().actorOf(new Props(new TestSenderAccumulatorFactory(latch)), refName);
		}
		
		return accumulator;		
	}
	
	protected int getAccumulated(ActorRef accumulator) {
		
		int value = 0;
		
		Future<Object> future = Patterns.ask(accumulator, new Retrieve(), Timeout.intToTimeout(2000));
		try {
			Object response = Await.result(future, Duration.parse("2 seconds"));
			if(response instanceof Integer) {
				value = (Integer)response;
			} else {
				Assert.fail("Accumulator responded with non-integer value");
			}
		} catch (Exception e) {
			Assert.fail("Took too long to respond with accumulated value: " + e.getMessage());
		}		
		
		return value;
	}
	
	
}
