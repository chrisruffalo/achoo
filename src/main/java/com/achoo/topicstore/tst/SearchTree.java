package com.achoo.topicstore.tst;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class SearchTree<D> implements SearchNode<D> {

	private InternalNode<D> root;

	@Override
	public Set<D> lookup(String key, boolean exact) {
		Set<D> results = new TreeSet<D>();
		this.lookup(results, key, exact);
		return results;
	}
	
	@Override
	public void lookup(Set<D> accumulator, String key, boolean exact) {
		if(this.root != null) {
			this.root.lookup(accumulator, key, exact);
		}		
	}

	@Override
	public void add(String key, D value) {
		this.add(key, Collections.singleton(value));
	}

	@Override
	public void add(String key, D[] value) {
		this.add(key, Arrays.asList(value));
	}

	@Override
	public void add(String key, Collection<D> value) {
		if(key == null || key.isEmpty()) {
			return;
		}
		int index = 0;
		char[] array = key.toCharArray();
		if(this.root == null) {
			this.root = NodeFactory.create(array[0]);
		}
		this.root.add(array, index, value);
	}

	@Override
	public void print() {
		if(this.root != null) {
			this.root.print();
		}
	}
	
}
