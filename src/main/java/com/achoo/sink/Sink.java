package com.achoo.sink;

import com.achoo.model.Token;

public abstract class Sink implements ISink {

	@Override
	public void onEvent(Token event, long sequence, boolean endOfBatch)	throws Exception {
		this.sink(event);
	}

}
