package com.achoo.topicstore;

import java.util.Collection;

public interface StructuredTopic {

	boolean matchesPart(String topic);
	
	StructuredTopic parent();
	
	Collection<StructuredTopic> children();
	
	String uuid();
	
	void add(StructuredTopic topic);
	
	long hash();
	
	String part();
	
}
