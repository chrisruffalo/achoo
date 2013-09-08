package com.achoo.topicstore.trie;


public class LiteralCharacterNode extends AbstractNode {

	private final Character value;	
	
	LiteralCharacterNode(Node parent, char literal) {
		super(parent);
		
		this.value = Character.valueOf(literal);
	}
	
	@Override
	public char value() {
		return this.value.charValue();
	}

	@Override
	public boolean matches(char input, boolean exact) {
		return this.value.equals(input);
	}

}
