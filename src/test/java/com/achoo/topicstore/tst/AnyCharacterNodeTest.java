package com.achoo.topicstore.tst;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class AnyCharacterNodeTest {

	@Test
	public void testBasicStructureAndLookup() {
		AnyCharacterNode<String> any = new AnyCharacterNode<>();
		
		any.add("#bc", "any");
		any.add("abc", "first");
		any.add("gbc", "second");
		any.print();
		
		LiteralNode<String> shunt = (LiteralNode<String>)any.shunt();
		
		Assert.assertNotNull(shunt);
		Assert.assertEquals(Character.valueOf('a'), shunt.point());
		
		// get b-point's children (c and a)
		LiteralNode<String> high = (LiteralNode<String>)shunt.high();
		LiteralNode<String> low = (LiteralNode<String>)shunt.low();
		
		Assert.assertNotNull(high);
		Assert.assertNull(low);
		
		// high is available
		Assert.assertEquals(Character.valueOf('b'), high.point());
		
		// now, for c, it should have gone shunt(a) -> high(b) -> high(c)
		LiteralNode<String> doubleHigh = (LiteralNode<String>)high.high();
		Assert.assertEquals(Character.valueOf('c'), doubleHigh.point());
		
		// so now, to the lookup
		Set<String> results = any.lookup("#bc", true);
		Assert.assertEquals(1, results.size());
		
		// no results, # character is mandatory but 'any'
		results = any.lookup("bc", false);
		Assert.assertEquals(0, results.size());
		
		results = any.lookup("xbc", false);
		Assert.assertEquals(1, results.size());
	
		results = any.lookup("gbc", false);
		Assert.assertEquals(2, results.size());
		
		results = any.lookup("abc", false);
		Assert.assertEquals(2, results.size());
	}
	
	@Test
	public void testRepeatingAnyCharacterNodes() {
		AnyCharacterNode<String> any = new AnyCharacterNode<>();
		
		any.add("a##c", "cee");
		any.add("a##d", "dee");
		any.add("a###", "all");
		
		Set<String> results = any.lookup("abbc", true);
		Assert.assertEquals(0, results.size());

		results = any.lookup("abdf", false);
		Assert.assertEquals(1, results.size());
		
		results = any.lookup("abdf", true);
		Assert.assertEquals(0, results.size());
		
		results = any.lookup("abbc", false);
		Assert.assertEquals(2, results.size());
		
		results = any.lookup("abbd", false);
		Assert.assertEquals(2, results.size());		
	}
}
