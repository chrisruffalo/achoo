package com.achoo.topicstore.tst.visitor;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.achoo.topicstore.tst.InternalNode;

public class SearchingVisitor<D> implements Visitor<D> {

	private Set<D> results;
	
	public SearchingVisitor() {
		this.results = null;
	}
	
	@Override
	public void at(InternalNode<D> node, int index, char[] key, boolean exact) {
		Set<D> foundAtNode = node.get(index);
		if(foundAtNode != null && !foundAtNode.isEmpty()) {
			// lazy init of set when it has some results
			if(this.results == null) {
				this.results = new LinkedHashSet<>();
			}
			this.results.addAll(node.get(index));
		}
	}
	
	public Set<D> found() {
		if(this.results == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(this.results);
	}

	@Override
	public boolean construct() {
		return false;
	}	
}
