package com.achoo.topicstore.trie;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class NodeFindTest {

	@Test
	public void testSimpleFind() {
		Node node = NodeFactory.generate("abcd");
		node.merge(NodeFactory.generate("abc"));
		node.merge(NodeFactory.generate("ab"));
		
		Set<Node> found = node.find("abcd");
		Assert.assertEquals(1, found.size());
		
		found = node.find("ab");
		Assert.assertEquals(1, found.size());
		
		found = node.find("abc");
		Assert.assertEquals(1, found.size());
		
		found = node.find("a");
		Assert.assertEquals(0, found.size());
	}
	
	@Test
	public void testSingleCharacterWildcards() {
		Node node = NodeFactory.generate("abcd");
		node.merge(NodeFactory.generate("ab#d"));
		node.merge(NodeFactory.generate("abed"));
		node.merge(NodeFactory.generate("a#c#"));
		
		Set<Node> found = node.find("abcd");
		Assert.assertEquals(3, found.size());
		
		found = node.find("abed");
		Assert.assertEquals(2, found.size());
	}
	
}
