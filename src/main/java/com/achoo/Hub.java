package com.achoo;

import java.util.concurrent.Executor;

import com.achoo.model.Item;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public abstract class Hub<I extends Item> {

	private Disruptor<I> disruptor;
	
	private Executor executor;

	public Hub(Executor sharedExecutor, int ringSize, EventFactory<I> eventFactory) {
		// save executor
		this.executor = sharedExecutor;
		
		// create and start the dispatch ring
		this.disruptor = new Disruptor<>(eventFactory, ringSize, this.executor);
	}
	
	public RingBuffer<I> getRingBuffer() {
		return this.disruptor.getRingBuffer();
	}
	
	public Executor getExecutor() {
		return this.executor;
	}
	
	protected Disruptor<I> getDisruptor() {
		return this.disruptor;
	}
}
