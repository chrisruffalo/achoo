package com.achoo.topicstore.trie;

import org.junit.Assert;
import org.junit.Test;


public class WildcardNodeTest {

	@Test
	public void testMatch() {
		WildcardNode node = new WildcardNode(null);
		Assert.assertTrue(node.matches('*'));
		Assert.assertTrue(node.matches('a'));
		Assert.assertTrue(node.matches('g'));
		Assert.assertTrue(node.matches('!'));
		Assert.assertTrue(node.matches('h'));
		Assert.assertTrue(node.matches('0'));
		Assert.assertTrue(node.matches(')'));
	}

	@Test
	public void testExactMatch() {
		WildcardNode node = new WildcardNode(null);
		Assert.assertTrue(node.matches('*', true));
		Assert.assertFalse(node.matches('a', true));
		Assert.assertFalse(node.matches('g', true));
		Assert.assertFalse(node.matches('!', true));
		Assert.assertFalse(node.matches('h', true));
		Assert.assertFalse(node.matches('0', true));
		Assert.assertFalse(node.matches(')', true));
	}
}
