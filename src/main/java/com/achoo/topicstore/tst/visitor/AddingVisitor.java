package com.achoo.topicstore.tst.visitor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.achoo.topicstore.tst.InternalNode;

public class AddingVisitor<D> implements Visitor<D> {

	private Set<D> values;
	
	public AddingVisitor(D value) {
		this(Collections.singleton(value));
	}
	
	public AddingVisitor(D[] values) {
		this(Arrays.asList(values));
	}
	
	public AddingVisitor(Collection<D> values) {
		this.values = new LinkedHashSet<>();
		this.values.addAll(values);
	}
	
	@Override
	public void at(InternalNode<D> node, int index, char[] key, boolean exact) {
		node.add(index, this.values);
	}
	
	@Override
	public boolean construct() {
		return true;
	}

}
