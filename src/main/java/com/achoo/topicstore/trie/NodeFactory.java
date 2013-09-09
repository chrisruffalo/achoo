package com.achoo.topicstore.trie;

import com.google.common.base.Strings;

public final class NodeFactory {
	
	//private static final Logger LOGGER = LoggerFactory.getLogger(NodeFactory.class);
	
	private NodeFactory() {
		
	}
	
	public static Node generate(String input) {
		Node root = new RootNode();
		
		// return empty root if no good input provided
		if(Strings.isNullOrEmpty(input)) {
			return root;
		}
		
		
		Node previous = root;
		
		for(int i = 0; i < input.length(); i++) {
			char current = input.charAt(i);
			Node local = null;
			if(AnyCharacterNode.ANYCHARACTER == current) {
				local = new AnyCharacterNode(previous);
			} else if(WildcardNode.WILDCARD == current) {
				local = new WildcardNode(previous);
			} else {
				local = new LiteralCharacterNode(previous, current);
			}
			
			previous.merge(local);
			previous = local;
			
			//NodeFactory.LOGGER.info("local: {}, parent: {}", local.toString(), local.parent().toString());
		}
		// previous is, at this point, the highest/deepest point
		previous.merge(new TerminationNode(previous));

		// return the root
		return root;
	}	
}
