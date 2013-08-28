package com.achoo.sink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.achoo.model.Token;

public class LoggingSink extends Sink {

	private Logger logger = LoggerFactory.getLogger(LoggingSink.class); 
	
	private String name;
	
	public LoggingSink(String name) {
		this.name = name;
	}
	
	@Override
	public void sink(Token token) {
		this.logger.info("[{}] token:{}", this.name, token.getUuid());
	}

}
