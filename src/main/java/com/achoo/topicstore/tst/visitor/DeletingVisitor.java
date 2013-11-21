package com.achoo.topicstore.tst.visitor;

import com.achoo.topicstore.tst.InternalNode;

public class DeletingVisitor<D> implements Visitor<D> {

	@Override
	public void at(InternalNode<D> node, int index, char[] key, boolean exact) {
		
	}

	@Override
	public boolean construct() {
		return false;
	}

}
