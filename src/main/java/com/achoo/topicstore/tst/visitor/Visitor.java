package com.achoo.topicstore.tst.visitor;

import com.achoo.topicstore.tst.InternalNode;

public interface Visitor<D> {

	void at(InternalNode<D> node, int index, char[] key, boolean exact);
	
	boolean construct();
	
}
