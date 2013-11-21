package com.achoo.topicstore.tst;

import java.util.Collection;
import java.util.Set;

public interface SearchNode<D> {

	Set<D> find(String key, boolean exact);
	
	void put(String key, D value);
	
	void put(String key, D[] values);
	
	void put(String key, Collection<D> values);
	
	void print();

}
