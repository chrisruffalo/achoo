package com.achoo.topicstore.trie;

import com.achoo.exceptions.AchooRuntimeException;

public class InvalidMergeException extends AchooRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidMergeException(String string) {
		super(string);
	}

}
