package com.achoo.topicstore.trie;


public class WildcardNode extends AbstractNode {

	private static final char WILDCARD = '*';
	
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
}
