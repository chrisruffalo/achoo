package com.achoo.topicstore.tst.matcher;

import java.util.TreeMap;

public class LiteralCharacterMatcher implements Matcher {

	private static TreeMap<Character, Matcher> matcherPool = new TreeMap<>();
	
	public static Matcher create(char local) {
		Character character = Character.valueOf(local);
		Matcher matcher = LiteralCharacterMatcher.matcherPool.get(character);
		if(matcher == null) {
			matcher = new LiteralCharacterMatcher(character);
			LiteralCharacterMatcher.matcherPool.put(character, matcher);
		}
		return matcher;
	}
	
	private Character local;
	
	private LiteralCharacterMatcher(char character) {
		this(Character.valueOf(character));
	}
	
	private LiteralCharacterMatcher(Character character) {
		this.local = character;
	}
	
	@Override
	public boolean match(Character input, boolean exact) {
		return this.local.equals(input);
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
		return false;
	}
}
