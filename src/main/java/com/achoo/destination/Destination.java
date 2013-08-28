package com.achoo.destination;

import com.achoo.model.Token;
import com.lmax.disruptor.EventHandler;

public abstract class Destination implements EventHandler<Token> {

	protected abstract void handle(Token token, long sequence);
	
	private String destinationKey;
	
	private Destination() {
		
	}
	
	public Destination(String destinationKey) {
		this();
		
		// throw an error if a bad destination key is used
		if(destinationKey == null || destinationKey.isEmpty()) {
			throw new IllegalArgumentException("A " + this.getClass().getSimpleName().toLowerCase() + " must be created with a non-null, non-empty destination key.");
		}
		
		this.destinationKey = destinationKey;
	}
	
	public String getDestinationKey() {
		return destinationKey;
	}
	
	@Override
	public void onEvent(Token event, long sequence, boolean endOfBatch)	throws Exception {
		// if the key matches
		if(event.getDestinationKey().equals(this.destinationKey)) {
			// handle the event
			this.handle(event, sequence);
		}		
	}	
}
