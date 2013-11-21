package com.achoo.topicstore.tst;

import org.junit.Test;

public class SearchTreeTest extends AbstractTernaryTestCase {

	@Test
	public void doubleRepeatingAnyAndLiteralTest() {
		
		SearchTree<String> tree = new SearchTree<>();
		tree.put("a####z", "repeat-wild");
		tree.put("abcdez", "straight");
		tree.put("abc#ez", "one-wild");
		tree.put("aa#z#z", new String[]{"double-a", "double-double"});
		tree.put("aa##zz", "aa-zzzzzzz");
		tree.put("f", "f1");
		tree.put("ff", "f2");
		tree.put("fff", "f3");
		tree.put("ffff", "f4");
		tree.put("fffff", "f5");
		
		// these should not (exact) match
		this.check(tree, 0, "a##z", true);
		this.check(tree, 0, "abcd#z", true);
		this.check(tree, 0, "aaaaa", true);
		this.check(tree, 0, "aazz", true);
		
		// these should exact match
		this.check(tree, 1, "a####z", true, "repeat-wild");
		this.check(tree, 1, "abcdez", true, "straight");
		this.check(tree, 1, "abc#ez", true, "one-wild");
		this.check(tree, 2, "aa#z#z", true, "double-a", "double-double");
		this.check(tree, 1, "aa##zz", true, "aa-zzzzzzz");
		
		// checking repeats
		this.check(tree, 1, "f", true, "f1");
		this.check(tree, 1, "f", false, "f1");
		this.check(tree, 1, "fff", true, "f3");
		this.check(tree, 1, "fff", false, "f3");
		this.check(tree, 1, "fffff", true, "f5");
		this.check(tree, 1, "fffff", false, "f5");
		this.check(tree, 0, "ffffffff", true);
		this.check(tree, 0, "ffffffff", false);
		
		// this is actually pretty awesome because it
		// happens to be a very specially crafted string
		// that slots right into a bug case.
		// (there are changes in LiteralNode's traversal
		// pattern that resulted from this.)
		this.check(tree, 0, "bcdefz", false);
		
		// these should not inexact match
		this.check(tree, 0, "acz", false);
		this.check(tree, 0, "accz", false);
		this.check(tree, 0, "acccz", false);
		this.check(tree, 0, "aazcz", false);
		
		// these should work with inexact match
		this.check(tree, 3, "abcdez", false, "repeat-wild", "straight", "one-wild");
	}
}
