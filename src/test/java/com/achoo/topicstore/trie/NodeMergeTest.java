package com.achoo.topicstore.trie;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class NodeMergeTest {

	@Test
	public void testNoPath() {
		Node node = NodeFactory.generate("");
		Assert.assertEquals(0, node.paths().size());
	}
	
	@Test
	public void testOneCharacterPath() {
		Node node = NodeFactory.generate("a");
		Assert.assertEquals(1, node.paths().size());
	}
	
	@Test
	public void testSimplePath() {
		Node node = NodeFactory.generate("abcd");
		Assert.assertEquals(4, node.paths().size());		
	}
	
	@Test
	public void testMultiplePaths() {
		Node node1 = NodeFactory.generate("abcd");
		Node node2 = NodeFactory.generate("abef");
		Node node3 = NodeFactory.generate("abcg");
		Node node4 = NodeFactory.generate("abcd");
		Node node5 = NodeFactory.generate("aqlm#421*");
		Node node6 = NodeFactory.generate("1234");
		Node node7 = NodeFactory.generate("aaaaaaaaaaaaaaaaaaaaaaaa");
		Node node8 = NodeFactory.generate("999999999999999999999999");
		
		node1.merge(node2);
		node1.merge(node3);
		node1.merge(node4);
		node1.merge(node5);
		node1.merge(node6);
		node1.merge(node7);
		node1.merge(node8);
		
		Set<String> paths = node1.paths();
		Assert.assertEquals(66, paths.size());		
	}

	@Test(expected=InvalidMergeException.class)
	public void testBadMerge() {
		Node node1 = NodeFactory.generate("abcd");
		Node node2 = NodeFactory.generate("abef");
		Node nonRoot = node2.children().values().iterator().next();
		nonRoot.merge(node1);
	}
}
