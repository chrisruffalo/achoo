package com.achoo.topicstore.tst;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class SearchTree<D> implements SearchNode<D> {

	private InternalNode<D> root;

	@Override
	public Set<D> find(String key, boolean exact) {
		if(this.root != null) {
			return this.root.find(key, exact);
		}
		return Collections.emptySet();
	}

	@Override
	public void put(String key, D value) {
		this.put(key, Collections.singleton(value));
	}

	@Override
	public void put(String key, D[] values) {
		if(values == null || values.length == 0) {
			return;
		}
		this.put(key, Arrays.asList(values));
	}

	@Override
	public void put(String key, Collection<D> values) {
		if(values == null || values.isEmpty()) {
			return;
		}
		
		if(key == null || key.isEmpty()) {
			return;
		}
		
		if(this.root == null) {
			char[] keyArray = key.toCharArray();
			Character local = keyArray[0];
			this.root = NodeFactory.create(local);
		}
		
		this.root.put(key, values);
	}

	@Override
	public void print() {
		if(this.root != null) {
			this.root.print();
		}
	}
}
