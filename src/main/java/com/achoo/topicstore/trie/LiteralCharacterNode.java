package com.achoo.topicstore.trie;


public class LiteralCharacterNode extends AbstractNode {

	private char value;	
	
	LiteralCharacterNode(Node parent, char literal) {
		super(parent);
		
		this.value = literal;
	}
	
	@Override
	public char value() {
		return this.value;
	}

	@Override
	public boolean matches(char input, boolean exact) {
		return Character.compare(input, this.value()) == 0;
	}

}
