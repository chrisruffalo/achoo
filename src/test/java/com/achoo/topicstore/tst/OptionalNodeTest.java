package com.achoo.topicstore.tst;

import org.junit.Assert;
import org.junit.Test;

public class OptionalNodeTest extends AbstractTernaryTestCase {

	@Test
	public void testBasicOptional() {
		SearchTree<String> test = new SearchTree<>();
		
		Assert.assertTrue(test.configuration().optional().contains('?'));
		
		test.put("#?", "duo-1");
		test.put("?bc", "one");
		test.put("a?c", "two");
		test.put("ab?", "two-two");
		test.put("abc", "thr");
		test.put("efg", "for");
		test.put("afg", "fiv");
		test.put("bfg", "six");
		test.put("#?", "duo-2");
		test.put("??", "duo-3");
		test.put("z?????z", "zman");
		
		test.print();
		
		// do the usual exact matches
		this.check(test, 1, "?bc", true, "one");
		this.check(test, 1, "a?c", true, "two");
		this.check(test, 1, "abc", true, "thr");
		this.check(test, 1, "ab?", true, "two-two");
		this.check(test, 1, "??", true);
		
		// not expecting matches not in that group
		this.check(test, 0, "aaa", true);
		this.check(test, 0, "?bcd", true);
		this.check(test, 0, "?bb", true);
		this.check(test, 0, "def", true);
		this.check(test, 0, "???", true);
		this.check(test, 0, "?b", true);
		this.check(test, 0, "abcdefg", true);
		
		// expecting some basic other matches
		this.check(test, 4, "abc", false, "one", "two", "two-two", "thr");
		this.check(test, 1, "ebc", false, "one");
		this.check(test, 1, "jbc", false, "one");
		this.check(test, 1, "Xbc", false, "one");
		this.check(test, 1, "dbc", false, "one");
		
		// ? acts as a literal but should be matched by 'optional'
		this.check(test, 1, "a?c", false, "two");
		
		// checking mid-stream ?
		this.check(test, 1, "aec", false, "two");
		this.check(test, 1, "aXc", false, "two");
		this.check(test, 1, "a4c", false, "two");
		this.check(test, 0, "d4c", false);
		
		// testing stacked ?s
		//this.check(test, 4, "zz", false, "duo-1", "duo-2", "two-two", "duo-3", "zman");
		this.check(test, 1, "zXz", false, "zman");
		this.check(test, 1, "zXXz", false, "zman");
		this.check(test, 1, "zXXXz", false, "zman");
		this.check(test, 1, "zXXXXz", false, "zman");
		this.check(test, 1, "zXXXXXz", false, "zman");
		this.check(test, 0, "zXXXXXXz", false);
		
		// exact searches with stacked ?'s
		this.check(test, 0, "zXXXXXz", true);
		this.check(test, 1, "z?????z", true, "zman");
		//this.check(test, 0, "z????z", true);
		//this.check(test, 0, "z???z", true);
		//this.check(test, 0, "z??z", true);
		//this.check(test, 0, "z?z", true);
		//this.check(test, 0, "zz", true);		
		
		// checking end optional
		//this.check(test, 4, "ab", false, "duo-1", "duo-2", "two-two", "duo-3");
		//this.check(test, 3, "d", false, "duo-1", "duo-2", "duo-3");
	}
	
}
