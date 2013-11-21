package com.achoo.topicstore.tst;

import com.achoo.topicstore.tst.config.SearchConfiguration;
import com.achoo.topicstore.tst.matcher.AnyCharacterMatcher;
import com.achoo.topicstore.tst.matcher.LiteralCharacterMatcher;
import com.achoo.topicstore.tst.matcher.Matcher;

public final class NodeFactory {

	private NodeFactory() {
		// cannot construct
	}
	
	static <D> InternalNode<D> create(InternalNode<D> parent, Character local, SearchConfiguration configuration) {
		
		final InternalNode<D> node;
		if(configuration.wildcards().contains(local)) {
			Matcher matcher = new AnyCharacterMatcher(local);
			node = new OptionalNode<>(parent, matcher, true, configuration);
		} else if(configuration.optional().contains(local)) {
			Matcher matcher = new AnyCharacterMatcher(local);
			node = new OptionalNode<>(parent, matcher, false, configuration);
		} else if(configuration.any().contains(local)) {
			Matcher matcher = new AnyCharacterMatcher(local);
			node = new DirectionalNode<>(matcher, configuration);
		} else {
			Matcher matcher = new LiteralCharacterMatcher(local);
			node = new DirectionalNode<>(matcher, configuration);
		}
		
		//System.out.println(node.getClass().getName());
		return node;
	}
}
