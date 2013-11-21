package com.achoo.topicstore.tst.matcher;

public interface Matcher {

	boolean match(Character input, boolean exact);
	
	int compare(Character input);
	
	Character value();
	
	boolean attracts(boolean exact);
	
}
