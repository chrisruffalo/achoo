package com.achoo.topicstore.tst;


import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class LiteralNodeTest {

	@Test
	public void testBasicAdd() {
		LiteralNode<String> base = new LiteralNode<>('q');
		
		Assert.assertEquals(Character.valueOf('q'), base.point());
		Assert.assertNull(base.low());
		Assert.assertNull(base.high());
		
		base.add("qa", (String)null);
		Assert.assertNull(base.high());
		
		LiteralNode<String> low = (LiteralNode<String>)base.low();
		Assert.assertNotNull(low);
		Assert.assertEquals(Character.valueOf('a'), low.point());
		
		base.add("qb", (String)null);
		Assert.assertNotNull(low.high());
		Assert.assertNull(low.low());
	}

	@Test
	public void testLiteralLookup() {
		LiteralNode<String> base = new LiteralNode<>('c');
		base.add("cat", "valueCat");
		base.add("cot", "valueCot");
		base.add("cut", "valueCut");
		
		Assert.assertEquals("valueCat", base.lookup("cat", true).iterator().next());
		Assert.assertEquals("valueCot", base.lookup("cot", true).iterator().next());
		Assert.assertEquals("valueCut", base.lookup("cut", true).iterator().next());
	}
	
	@Test
	public void testAdvancedAdd() {
		LiteralNode<String> base = new LiteralNode<>('q');
		base.add("qq", "quit");
		Assert.assertNull(base.low());
		Assert.assertNull(base.high());

		base.add("qqq", "really");
		Assert.assertNull(base.low());
		Assert.assertNull(base.high());
	}

	@Test
	public void testRepeatingLookup() {
		LiteralNode<String> base = new LiteralNode<>('c');
		base.add("co", "company");
		base.add("coo", "dove");
		base.add("cooo", "longer");
		base.add("coooo", "longest");
		
		Set<String> results = base.lookup("co", true);
		Assert.assertEquals(1, results.size());
		
		results = base.lookup("co", true);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals("company", results.iterator().next());

		results = base.lookup("coo", true);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals("dove", results.iterator().next());
		
		results = base.lookup("cooo", true);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals("longer", results.iterator().next());
		
		results = base.lookup("coooo", true);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals("longest", results.iterator().next());
	}
}

