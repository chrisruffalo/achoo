package com.achoo.topicstore.trie;

import com.google.common.base.Strings;

public final class NodeFactory {
	
	//private static final Logger LOGGER = LoggerFactory.getLogger(NodeFactory.class);
	
	private NodeFactory() {
		
	}
	
	public static Node generate(String input) {
		RootNode root = new RootNode();
		
		// return empty root if no good input provided
		if(Strings.isNullOrEmpty(input)) {
			return root;
		}
		
		Node previous = root;
		
		for(char current : input.toCharArray()) {
			Node local = null;
			if('#' == current) {
				AnyCharacterNode any = new AnyCharacterNode(previous);
				any.root(root);
				local = any;
			} else if('*' == current) {
				WildcardNode wild = new WildcardNode(previous);
				wild.root(root);
				local = wild;
			} else {
				LiteralCharacterNode literal = new LiteralCharacterNode(previous, current);
				literal.root(root);
				local = literal;
			}
			
			//NodeFactory.LOGGER.trace("local: " + local.toString());

			previous.merge(local);
			previous = local;
		}
		// previous is, at this point, the highest/deepest point
		previous.merge(new TerminationNode(previous));
		
		// return the root
		return root;
	}	
}
