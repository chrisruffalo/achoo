package com.achoo.topicstore.tst;

import org.junit.Test;

public class SearchTreeTest extends AbstractTernaryTestCase {

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
		this.check(tree, 0, "a##z", true);
		this.check(tree, 0, "abcd#z", true);
		this.check(tree, 0, "aaaaa", true);
		this.check(tree, 0, "aazz", true);
		
		// these should exact match
		this.check(tree, 1, "a####z", true, "repeat-wild");
		this.check(tree, 1, "abcdez", true, "straight");
		this.check(tree, 1, "abc#ez", true, "one-wild");
		this.check(tree, 2, "aa#z#z", true, "doulbe-a", "double-double");
		this.check(tree, 1, "aa##zz", true, "aa-zzzzzzz");
		
		// these should not inexact match
		this.check(tree, 0, "bcdefz", false);
		this.check(tree, 0, "acz", false);
		this.check(tree, 0, "accz", false);
		this.check(tree, 0, "acccz", false);
		this.check(tree, 0, "aazcz", false);
		
		// these should work with inexact match
		this.check(tree, 3, "abcdez", false, "repeat-wild", "straight", "one-wild");
	}
}
