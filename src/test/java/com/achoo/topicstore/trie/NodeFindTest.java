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
	
	@Test
	public void testBasicWildcard() {
		Node node = NodeFactory.generate("abcd");
		node.merge(NodeFactory.generate("a*d"));
		node.merge(NodeFactory.generate("a*cd"));
		node.merge(NodeFactory.generate("ab*d"));
		node.merge(NodeFactory.generate("abc*"));
		node.merge(NodeFactory.generate("a*c*"));
		node.merge(NodeFactory.generate("*b*d"));
		node.merge(NodeFactory.generate("*bcd"));
		
		// ensure that wildcards can mix
		node.merge(NodeFactory.generate("#*cd"));
		node.merge(NodeFactory.generate("##*d"));
		node.merge(NodeFactory.generate("#*#d"));
		node.merge(NodeFactory.generate("#*d"));
		
		// ensure backward compatibility
		node.merge(NodeFactory.generate("###d"));
		
		// full wildcard
		node.merge(NodeFactory.generate("*"));
		
		// do not match
		node.merge(NodeFactory.generate("eeee"));
		node.merge(NodeFactory.generate("e*"));
		node.merge(NodeFactory.generate("*e"));

		// make sure root is calculated properly
		Assert.assertEquals(4, node.children().size());
		
		Set<Node> found = node.find("abcd");
		Assert.assertEquals(14, found.size());		
	}
	
	@Test
	public void testOnlyHasWildcards() {
		Node node = NodeFactory.generate("*");
		node.merge(NodeFactory.generate("a*d"));
		node.merge(NodeFactory.generate("a*cd"));
		node.merge(NodeFactory.generate("ab*d"));
		node.merge(NodeFactory.generate("abc*"));
		node.merge(NodeFactory.generate("a*c*"));
		node.merge(NodeFactory.generate("*b*d"));
		node.merge(NodeFactory.generate("*bcd"));
		
		// ensure that wildcards can mix
		node.merge(NodeFactory.generate("#*cd"));
		node.merge(NodeFactory.generate("##*d"));
		node.merge(NodeFactory.generate("#*#d"));
		
		// ensure backward compatibility
		node.merge(NodeFactory.generate("###d"));
		
		// do not match
		node.merge(NodeFactory.generate("e*"));
		node.merge(NodeFactory.generate("*e"));

		// make sure root is calculated properly
		Assert.assertEquals(4, node.size());
		
		Set<Node> found = node.find("abcd");
		Assert.assertEquals(12, found.size());		
	}
	
	@Test
	public void testExactWithWildcards() {
		Node node = NodeFactory.generate("abcd");
		node.merge(NodeFactory.generate("a*d"));
		node.merge(NodeFactory.generate("a*cd"));
		node.merge(NodeFactory.generate("ab*d"));
		node.merge(NodeFactory.generate("abc*"));
		node.merge(NodeFactory.generate("a*c*"));
		node.merge(NodeFactory.generate("*b*d"));
		node.merge(NodeFactory.generate("*bcd"));
		
		// ensure that wildcards can mix
		node.merge(NodeFactory.generate("#*cd"));
		node.merge(NodeFactory.generate("##*d"));
		node.merge(NodeFactory.generate("#*#d"));
		
		// ensure backward compatibility
		node.merge(NodeFactory.generate("###d"));
		
		// full wildcard
		node.merge(NodeFactory.generate("*"));
		
		// do not match
		node.merge(NodeFactory.generate("e*"));
		node.merge(NodeFactory.generate("*e"));

		// make sure root is calculated properly
		for(Node child : node.children()) {
			System.out.println("child : " + child.getClass().getName());
		}
		Assert.assertEquals(4, node.children().size());
		
		Set<Node> found = node.find("abcd", true);
		Assert.assertEquals(1, found.size());		
		
		found = node.find("a*d", true);
		Assert.assertEquals(1, found.size());
		
		found = node.find("#*cd", true);
		Assert.assertEquals(1, found.size());
	}
	
}
