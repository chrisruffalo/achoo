package com.achoo.broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

import com.achoo.Hub;
import com.achoo.destination.Destination;
import com.achoo.destination.Queue;
import com.achoo.destination.Topic;
import com.achoo.model.Token;
import com.lmax.disruptor.dsl.Disruptor;

public class Dispatcher extends Hub<Token> {

	private HashMap<String, Queue> queues;
	private HashMap<String, Topic> topics;
	
	private Semaphore disruptorChangeSemaphore;
	
	public Dispatcher(Executor executor, int ringSize) {
		// build hub
		super(executor, ringSize, Token.getTokenFactory());
		
		// create semaphore
		this.disruptorChangeSemaphore = new Semaphore(1, true);
		
		// create handler group
		this.queues = new HashMap<>();
		this.topics = new HashMap<>();
	}
	
	public void createQueue(String destinationKey) {
		Queue queue = new Queue(destinationKey);
		this.attachDestination(destinationKey, queue, this.queues);
	}
	
	public void createTopic(String destinationKey) {
		Topic topic = new Topic(destinationKey);
		this.attachDestination(destinationKey, topic, this.topics);
	}
	
	public void removeQueue(String destinationKey) {
		this.removeDestination(destinationKey, this.queues);
	}
	
	public void removeTopic(String destinationKey) {
		this.removeDestination(destinationKey, this.topics);
	}
	
	private <D extends Destination> void attachDestination(String destinationKey, D destination, HashMap<String, D> destinations) {
		try {
			this.disruptorChangeSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		
		// do not attach destination if it already exists
		if(destinations.containsKey(destinationKey)) {
			this.disruptorChangeSemaphore.release();
			return;
		}
				
		// add the destination
		destinations.put(destinationKey, destination);
	
		// modify handler list
		this.modifyHandlers();
		
		this.disruptorChangeSemaphore.release();
	}
	
	private <D extends Destination> void removeDestination(String destinationKey, HashMap<String, D> destinations) {
		try {
			this.disruptorChangeSemaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		
		// do not remove if the key is not in the list
		if(!destinations.containsKey(destinationKey)) {
			this.disruptorChangeSemaphore.release();
			return;
		}
				
		// add the destination
		destinations.remove(destinationKey);
	
		// modify handler list
		this.modifyHandlers();
		
		this.disruptorChangeSemaphore.release();
	}
	
	private void modifyHandlers() {
		if(this.disruptorChangeSemaphore.tryAcquire()) {
			throw new IllegalAccessError("The lock must be acquired before handlers can be accessed");
		}
		
		// get values
		List<Destination> destinations = new ArrayList<>(this.queues.size() + this.topics.size());
		destinations.addAll(this.queues.values());
		destinations.addAll(this.topics.values());
		
		// re-intialize the disruptor's handler set
		Disruptor<Token> disruptor = this.getDisruptor();
		synchronized (disruptor) {
			// create an array from hacked nothing
			Destination[] handlers = destinations.toArray(new Destination[destinations.size()]);
			
			// update disruptor with new handler list
			disruptor.handleEventsWith(handlers);
		}			
		
	}
}
