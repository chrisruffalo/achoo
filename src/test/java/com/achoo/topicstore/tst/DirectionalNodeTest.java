package com.achoo.topicstore.tst;

import org.junit.Assert;
import org.junit.Test;

import com.achoo.topicstore.tst.config.DefaultSearchConfiguration;
import com.achoo.topicstore.tst.matcher.AnyCharacterMatcher;
import com.achoo.topicstore.tst.matcher.LiteralCharacterMatcher;

public class DirectionalNodeTest extends AbstractTernaryTestCase {

	@Test
	public void testLiteralBasicAdd() {
		DirectionalNode<String> base = new DirectionalNode<>(new LiteralCharacterMatcher('q'), new DefaultSearchConfiguration());

		Assert.assertEquals(Character.valueOf('q'), base.value());
		Assert.assertNull(base.low());
		Assert.assertNull(base.high());

		base.put("qa", (String) null);
		Assert.assertNull(base.high());

		DirectionalNode<String> low = (DirectionalNode<String>) base.low();
		DirectionalNode<String> same = (DirectionalNode<String>) base.same();
		DirectionalNode<String> high = (DirectionalNode<String>) base.high();
		Assert.assertNull(low);
		Assert.assertNull(high);
		Assert.assertNotNull(same);
		Assert.assertEquals(Character.valueOf('a'), same.value());

		base.put("qb", (String) null);
		Assert.assertNull(same.low());
		Assert.assertNotNull(same.high());
	}

	@Test
	public void testLiteralLookup() {
		DirectionalNode<String> base = new DirectionalNode<>(new LiteralCharacterMatcher('q'), new DefaultSearchConfiguration());
		base.put("cat", "valueCat");
		base.put("cot", "valueCot");
		base.put("cut", "valueCut");

		this.check(base, 1, "cat", true, "valueCat");
		this.check(base, 1, "cot", true, "valueCot");
		this.check(base, 1, "cut", true, "valueCut");

		// fuzzy match should have no bearing
		this.check(base, 1, "cat", false, "valueCat");
		this.check(base, 1, "cot", false, "valueCot");
		this.check(base, 1, "cut", false, "valueCut");
	}

	@Test
	public void testLiteralRepeatingAdd() {
		DirectionalNode<String> base = new DirectionalNode<>(new LiteralCharacterMatcher('q'), new DefaultSearchConfiguration());
		base.put("qq", "quit");
		Assert.assertNull(base.low());
		Assert.assertNull(base.high());

		base.put("qqq", "really");
		Assert.assertNull(base.low());
		Assert.assertNull(base.high());
	}

	@Test
	public void testLiteralRepeatingLookup() {
		DirectionalNode<String> base = new DirectionalNode<>(new LiteralCharacterMatcher('c'), new DefaultSearchConfiguration());
		base.put("co", "company");
		base.put("coo", "dove");
		base.put("cooo", "longer");
		base.put("coooo", "longest");

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
	
	@Test
    public void testAnyCharacterStructureAndLookup() {
	    DirectionalNode<String> any = new DirectionalNode<>(new AnyCharacterMatcher('#'), new DefaultSearchConfiguration());
	    
	    any.put("#bc", "any");
	    any.put("abc", "first");
	    any.put("gbc", "second");
	    
	    DirectionalNode<String> firstBranch = (DirectionalNode<String>)any.high();
	    
	    Assert.assertNotNull(firstBranch);
	    Assert.assertEquals(Character.valueOf('a'), firstBranch.value());
	    
	    // get b-point's children (c and a)
	    DirectionalNode<String> high = (DirectionalNode<String>)firstBranch.high();
	    DirectionalNode<String> low = (DirectionalNode<String>)firstBranch.low();
	    DirectionalNode<String> same = (DirectionalNode<String>)firstBranch.same();
	    
	    Assert.assertNotNull(high);
	    Assert.assertNotNull(same);
	    Assert.assertNull(low);
	    
	    // high is available
	    Assert.assertEquals(Character.valueOf('g'), high.value());
	    
	    // # -> b -> c -> g
	    DirectionalNode<String> highSame = (DirectionalNode<String>)high.same();
	    Assert.assertEquals(Character.valueOf('b'), highSame.value());
	    
	    // basic lookup
	    this.check(any, 1, "#bc", true, "any");
	    
	    // no results, # character is mandatory but 'any'
	    this.check(any, 0, "bc", true);
	    this.check(any, 0, "bc", false);
	
	    // wild card first character
	    this.check(any, 1, "xbc", false, "any");
	
	    // multi wildcard
	    this.check(any, 2, "gbc", false, "any", "second");
	    this.check(any, 2, "abc", false, "any", "first");
    }
    
    @Test
    public void testRepeatingAnyCharacterNodes() {
		DirectionalNode<String> any = new DirectionalNode<>(new AnyCharacterMatcher('#'), new DefaultSearchConfiguration());
	    
	    any.put("a##c", "cee");
	    any.put("a##d", "dee");
	    any.put("a###", "all");
	    
	    // comparison between exact and fuzzy
	    this.check(any, 0, "abbc", true);
	    this.check(any, 2, "abbc", false, "cee", "all");
	
	    // other misc checks
	    this.check(any, 1, "abdf", false, "all");
	    this.check(any, 0, "abdf", true);
	
	    // matching checks
	    this.check(any, 2, "abbc", false, "all", "cee");
	    this.check(any, 2, "abbd", false, "all", "dee");                                
    }
}