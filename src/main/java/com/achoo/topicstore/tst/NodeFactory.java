package com.achoo.topicstore.tst;

import com.achoo.topicstore.tst.matcher.LiteralCharacterMatcher;
import com.achoo.topicstore.tst.matcher.Matcher;

public final class NodeFactory {

	private NodeFactory() {
		// cannot construct
	}
	
	static <D> InternalNode<D> create(Character local) {
		
		final InternalNode<D> node;
		if(AnyCharacterNode.ANY_CHARACTER.equals(local)) {
			node = new AnyCharacterNode<>();
		} else {
			Matcher matcher = LiteralCharacterMatcher.create(local);
			node = new LiteralNode<>(matcher);
		}
		
		return node;
	}
}
