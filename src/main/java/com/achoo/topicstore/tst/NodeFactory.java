package com.achoo.topicstore.tst;

import com.achoo.topicstore.tst.matcher.AnyCharacterMatcher;
import com.achoo.topicstore.tst.matcher.LiteralCharacterMatcher;
import com.achoo.topicstore.tst.matcher.Matcher;

public final class NodeFactory {

	private static final Character ANY_CHARACTER = Character.valueOf('#');
	
	private NodeFactory() {
		// cannot construct
	}
	
	static <D> InternalNode<D> create(Character local) {
		
		final InternalNode<D> node;
		if(NodeFactory.ANY_CHARACTER.equals(local)) {
			Matcher matcher = new AnyCharacterMatcher(local);
			node = new DirectionalNode<>(matcher);
		} else {
			Matcher matcher = LiteralCharacterMatcher.create(local);
			node = new DirectionalNode<>(matcher);
		}
		
		return node;
	}
}
