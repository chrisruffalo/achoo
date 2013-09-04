package com.achoo.topicstore;

import com.achoo.exceptions.AchooRuntimeException;

public class InvalidTopicException extends AchooRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InvalidTopicException(String string) {
		super(string);
	}
	
}
