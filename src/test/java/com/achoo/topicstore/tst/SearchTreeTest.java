package com.achoo.topicstore.tst;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class SearchTreeTest {

	@Test
	public void doubleRepeatingAnyAndLiteralTest() {
		
		SearchTree<String> tree = new SearchTree<>();
		tree.add("a####z", "repeat-wild");
		tree.add("abcdez", "straight");
		tree.add("abc#ez", "one-wild");
		tree.add("aa#z#z", new String[]{"double-a", "double-double"});
		tree.add("aa##zz", "aa-zzzzzzz");
		
		tree.print();
		
		// these should not (exact) match
		this.lookupCheck(tree, 0, "a##z", true);
		this.lookupCheck(tree, 0, "abcd#z", true);
		this.lookupCheck(tree, 0, "aaaaa", true);
		this.lookupCheck(tree, 0, "aazz", true);
		
		// these should exact match
		this.lookupCheck(tree, 1, "a####z", true);
		this.lookupCheck(tree, 1, "abcdez", true);
		this.lookupCheck(tree, 1, "abc#ez", true);
		this.lookupCheck(tree, 2, "aa#z#z", true);
		this.lookupCheck(tree, 1, "aa##zz", true);
		
		// these should not inexact match
		this.lookupCheck(tree, 0, "bcdefz", false);
		this.lookupCheck(tree, 0, "acz", false);
		this.lookupCheck(tree, 0, "accz", false);
		this.lookupCheck(tree, 0, "acccz", false);
		this.lookupCheck(tree, 0, "aazcz", false);
		
		// these should work with inexact match
		this.lookupCheck(tree, 2, "aabbzz", false);
	}
	
	private void lookupCheck(SearchTree<String> tree, int expected, String key, boolean exact) {
		Set<String> results = tree.lookup(key, exact);
		if(expected != results.size()) {
			StringBuilder builder = new StringBuilder();
			builder.append("found: {");
			boolean first = true;
			for(String result : results) {
				if(!first) {
					builder.append(", ");
				}
				first = false;
				builder.append(result);
				
			}
			builder.append("}");
			System.out.println(builder.toString());
		}
		Assert.assertEquals(expected, results.size());
	}
	
}
