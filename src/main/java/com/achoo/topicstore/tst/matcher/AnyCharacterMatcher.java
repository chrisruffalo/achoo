package com.achoo.topicstore.tst.matcher;


public class AnyCharacterMatcher implements Matcher {

	private Character local;
	
	public AnyCharacterMatcher(char character) {
		this(Character.valueOf(character));
	}
	
	public AnyCharacterMatcher(Character character) {
		this.local = character;
	}
	
	@Override
	public boolean match(Character input, boolean exact) {
		if(exact) {
			return this.local.equals(input);
		}
		return true;
	}

	@Override
	public int compare(Character input) {
		return this.local.compareTo(Character.valueOf(input));
	}

	@Override
	public Character value() {
		return this.local;
	}
	
	@Override
	public boolean attracts(boolean exact) {
		return !exact;
	}
}
