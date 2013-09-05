package com.achoo.topicstore.trie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public final class NodeFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NodeFactory.class);
	
	private NodeFactory() {
		
	}
	
	public static Node generate(String input) {
		RootNode root = new RootNode();
		
		// return empty root if no good input provided
		if(Strings.isNullOrEmpty(input)) {
			return root;
		}
		
		Node previous = null;
		
		for(char current : input.toCharArray()) {
			Node local = null;
			if('#' == current) {
				AnyCharacterNode any = new AnyCharacterNode();
				any.root(root);
				local = any;
			} else if('*' == current) {
				WildcardNode wild = new WildcardNode();
				wild.root(root);
				local = wild;
			} else {
				LiteralCharacterNode literal = new LiteralCharacterNode(current);
				literal.root(root);
				local = literal;
			}
			
			NodeFactory.LOGGER.trace("local: " + local.toString());

			if(previous != null) {
				previous.merge(local);
			} else {
				root.merge(local);
			}
			previous = local;
		}
		// previous is, at this point, the highest/deepest point
		previous.merge(new TerminationNode(previous));
		
		// return the root
		return root;
	}	
}
