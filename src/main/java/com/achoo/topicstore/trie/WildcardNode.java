package com.achoo.topicstore.trie;

import java.util.Set;

import com.google.common.base.Strings;


public class WildcardNode extends AbstractNode {

	static final char WILDCARD = '*';
	
	WildcardNode(Node parent) {
		super(parent);
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
		boolean nullOrEmpty = Strings.isNullOrEmpty(input);
		
		if(nullOrEmpty || this.children().size() == 1) {
			Node termination = this.children().get(TerminationNode.TERMINATED);
			if(termination != null) {
				termination.find(destination, input, index, exact);
			}
			
			if(nullOrEmpty) {
				return;
			}
		}
		
		do {
			super.find(destination, input, index, exact);
			index++;
		} while(index < input.length());
	}
}
