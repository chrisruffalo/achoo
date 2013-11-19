package com.achoo.topicstore.tst;

import java.util.Collection;
import java.util.Set;

public interface SearchNode<D> {

	Set<D> lookup(String key, boolean exact);
	
	void lookup(Set<D> accumulator, String key, boolean exact);
	
	void add(String key, D value);
	
	void add(String key, D[] values);
	
	void add(String key, Collection<D> values);
	
	void print();

}
