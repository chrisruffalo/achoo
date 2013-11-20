package com.achoo.topicstore.tst;

import org.junit.Assert;
import org.junit.Test;

public class AnyCharacterNodeTest extends AbstractTernaryTestCase {

	@Test
	public void testBasicStructureAndLookup() {
		AnyCharacterNode<String> any = new AnyCharacterNode<>();
		
		any.add("#bc", "any");
		any.add("abc", "first");
		any.add("gbc", "second");
		
		LiteralNode<String> firstBranch = (LiteralNode<String>)any.high();
		
		Assert.assertNotNull(firstBranch);
		Assert.assertEquals(Character.valueOf('b'), firstBranch.point());
		
		// get b-point's children (c and a)
		LiteralNode<String> high = (LiteralNode<String>)firstBranch.high();
		LiteralNode<String> low = (LiteralNode<String>)firstBranch.low();
		
		Assert.assertNotNull(high);
		Assert.assertNotNull(low);
		
		// high is available
		Assert.assertEquals(Character.valueOf('c'), high.point());
		
		// # -> b -> c -> g
		LiteralNode<String> doubleHigh = (LiteralNode<String>)high.high();
		Assert.assertEquals(Character.valueOf('g'), doubleHigh.point());
		
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
		AnyCharacterNode<String> any = new AnyCharacterNode<>();
		
		any.add("a##c", "cee");
		any.add("a##d", "dee");
		any.add("a###", "all");
		
		// comparison between exact and fuzzy
		this.check(any, 0, "abbc", true);
		this.check(any, 2, "abbc", false, "cee", "all");

		// other misc checks
		this.check(any, 1, "abdf", false);
		this.check(any, 0, "abdf", true);

		// matching checks
		this.check(any, 2, "abbc", false, "all", "cee");
		this.check(any, 2, "abbd", false, "all", "dee");				
	}
}
