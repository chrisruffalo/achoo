package com.achoo.topicstore.trie;

import java.util.Map;
import java.util.Set;



public class WildcardNode extends AbstractNode {

	static final char WILDCARD = '*';
	
	WildcardNode(Node parent, Map<Character, Node> backingTable) {
		super(parent, backingTable);
	}
	
	@Override
	public char value() {
		return WildcardNode.WILDCARD;
	}
	
	@Override
	public boolean matches(char input, boolean exact) {
		if(exact) {
			return Character.compare(input, this.value()) == 0;
		}
		return true;
	}
	
	@Override
	public void find(Set<Node> destination, String input, int index, boolean exact) {
		if(!exact) {
			Node terminated = this.get(TerminationNode.TERMINATED);
			if(null != terminated) {
				destination.add(this);
				
				// fast forward to end of string
				// in the future this might mean
				// fast forwarding to a "." or "/"
				// character for hierarchical search
				//index = input.length();
				if(this.size() == 1) {
					index = input.length();
				}
			}
			
			if(index >= input.length()) {
				return;
			}
		}
		
		// search as per normal
		super.find(destination, input, index, exact);
		
		// only continue wildcard style search if the
		// search is not an exact match
		if(!exact && index < input.length()) {
			super.find(destination, input, index+1, exact);
		}
	}
}
