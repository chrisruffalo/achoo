package com.achoo.destination;

import com.achoo.model.Token;

public class Queue extends Destination {

	public Queue(String destinationKey) {
		super(destinationKey);
	}

	@Override
	protected void handle(Token token, long sequence) {
		
	}	
}
