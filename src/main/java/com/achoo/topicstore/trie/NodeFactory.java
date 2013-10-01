package com.achoo.topicstore.trie;

import gnu.trove.map.hash.THashMap;

import java.util.Map;

import com.google.common.base.Strings;

public final class NodeFactory {
	
	//private static final Logger LOGGER = LoggerFactory.getLogger(NodeFactory.class);
	
	private NodeFactory() {
		
	}
	
	public static Node generate(String input) {
			
		// create table that will back partition table
		Map<Long, Node> backer = new THashMap<>();
		SharedMarker marker = new SharedMarker();
		
		Node root = new RootNode(new PartitionMap(0, backer, marker));
		
		// return empty root if no good input provided
		if(Strings.isNullOrEmpty(input)) {
			return root;
		}		
		
		Node previous = root;
		
		for(int i = 0; i < input.length(); i++) {
			char current = input.charAt(i);
			
			// create partition (partition can't really start at 0 or 1 for safety			
			PartitionMap partition = new PartitionMap(i+1, backer, marker);
			
			Node local = null;
			if(AnyCharacterNode.ANYCHARACTER == current) {
				local = new AnyCharacterNode(previous, partition);
			} else if(WildcardNode.WILDCARD == current) {
				local = new WildcardNode(previous, partition);
			} else {
				local = new LiteralCharacterNode(previous, current, partition);
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
