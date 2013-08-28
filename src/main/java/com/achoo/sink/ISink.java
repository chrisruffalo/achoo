package com.achoo.sink;

import com.achoo.model.Token;
import com.lmax.disruptor.EventHandler;

public interface ISink extends EventHandler<Token> {

	void sink(Token token);
	
}
