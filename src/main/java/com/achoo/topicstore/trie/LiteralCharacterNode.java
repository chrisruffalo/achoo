package com.achoo.topicstore.trie;

import java.util.Map;


public class LiteralCharacterNode extends AbstractNode {

	private final Character value;	
	
	LiteralCharacterNode(Node parent, char literal, Map<Character, Node> backingTable) {
		super(parent, backingTable);
		
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
