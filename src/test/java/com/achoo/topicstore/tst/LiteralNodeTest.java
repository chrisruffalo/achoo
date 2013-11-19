package com.achoo.topicstore.tst;


import org.junit.Assert;
import org.junit.Test;

public class LiteralNodeTest extends AbstractTernaryTestCase {

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
		
		this.check(base, 1, "cat", true, "valueCat");
		this.check(base, 1, "cot", true, "valueCot");
		this.check(base, 1, "cut", true, "valueCut");
		
		// fuzzy match should have no bearing
		this.check(base, 1, "cat", false, "valueCat");
		this.check(base, 1, "cot", false, "valueCot");
		this.check(base, 1, "cut", false, "valueCut");
	}
	
	@Test
	public void testRepeatingAdd() {
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
		
		this.check(base, 1, "co", true, "company");
		this.check(base, 1, "coo", true, "dove");
		this.check(base, 1, "cooo", true, "longer");
		this.check(base, 1, "coooo", true, "longest");
		
		// exactness of match should have no impact
		this.check(base, 1, "co", false, "company");
		this.check(base, 1, "coo", false, "dove");
		this.check(base, 1, "cooo", false, "longer");
		this.check(base, 1, "coooo", false, "longest");
	}
}

