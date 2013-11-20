package com.achoo.topicstore.tst;


public class AnyCharacterNode<D> extends LiteralNode<D> {
	
	public static final Character ANY_CHARACTER = '#';
	
	public AnyCharacterNode() {
		super(AnyCharacterNode.ANY_CHARACTER);
	}
	
	@Override
	public boolean matches(char value, boolean exact) {
		if(exact) {
			return AnyCharacterNode.ANY_CHARACTER.equals(Character.valueOf(value));
		} 
		return true;
	}

	@Override
	public boolean extend(boolean exact) {
		return !exact;
	}
	
}
