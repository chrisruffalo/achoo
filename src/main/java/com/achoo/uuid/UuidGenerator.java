package com.achoo.uuid;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UuidGenerator {

	private static final int DEFAULT_MAX_UUIDS = 1024;
	
	private Logger logger;
	
	private BlockingQueue<String> uuids;
	
	private AtomicBoolean timeToStop;
	
	private Thread generatorThread;
	
	public UuidGenerator(int max) {
		this.uuids = new ArrayBlockingQueue<>(max);
		this.timeToStop = new AtomicBoolean(false);
		this.logger = LoggerFactory.getLogger(this.getClass());
		
		Runnable generator = new Runnable() {
			@Override
			public void run() {
				while(!timeToStop.get()) {
					String uuid = UUID.randomUUID().toString();
					try {
						if(uuids.remainingCapacity() == 0) {
							logger.debug("UUID Generator Queue is Full");
						}						
						uuids.put(uuid);
					} catch (InterruptedException e) {
						if(!timeToStop.get()) {
							throw new RuntimeException("Could not put more UUIDs", e);
						}
					}
				}
			}
		};		
		this.generatorThread = new Thread(generator);
	}
	
	public UuidGenerator() {
		this(UuidGenerator.DEFAULT_MAX_UUIDS);
	}
	
	public String get() {
		try {
			return this.uuids.take();
		} catch (InterruptedException e) {
			throw new RuntimeException("Could not get more UUIDs", e);
		}
	}
	
	public void start() {
		this.generatorThread.start();
	}
	
	public void stop() {
		this.timeToStop.set(true);
		this.generatorThread.interrupt();
	}
}
