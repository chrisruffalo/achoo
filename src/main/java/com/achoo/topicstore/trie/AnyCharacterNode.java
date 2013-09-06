package com.achoo.topicstore.trie;

public class AnyCharacterNode extends AbstractNode {

	AnyCharacterNode(Node parent) {
		super(parent);
	}

	static final char ANYCHARACTER = '#';
	
	@Override
	public char value() {
		return AnyCharacterNode.ANYCHARACTER;
	}

	@Override
	public boolean matches(char input, boolean exact) {
		if(exact) {
			return Character.compare(input, this.value()) == 0;
		}
		return true;
	}

}
