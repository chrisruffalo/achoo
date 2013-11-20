package com.achoo.topicstore.tst;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractNode<D> implements InternalNode<D> {
	
	public AbstractNode() {

	}
	
	public abstract boolean matches(char value, boolean exact);
	
	public Set<D> lookup(String key, boolean exact) {
		Set<D> results = new TreeSet<D>();
		this.lookup(results, key, exact);
		return results;
	}
	
	public void lookup(Set<D> accumulator, String key, boolean exact) {
		char[] keyArray = key.toCharArray();
		this.lookup(accumulator,  keyArray, 0, exact);
	}
	
	public void add(String key, D value) {
		this.add(key.toCharArray(), 0, Collections.singleton(value));
	}
	
	public void add(String key, D[] value) {
		this.add(key, Arrays.asList(value));
	}
	
	public void add(String key, Collection<D> value) {
		this.add(key.toCharArray(), 0, value);
	}
	
	public void print() {
        print("", "", true);
    }

}
