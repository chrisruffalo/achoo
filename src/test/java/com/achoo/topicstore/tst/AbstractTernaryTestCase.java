package com.achoo.topicstore.tst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;

public abstract class AbstractTernaryTestCase {

	public void check(SearchNode<String> tree, int expected, String key, boolean exact, String... match) {
		Set<String> results = tree.lookup(key, exact);
		if(expected != results.size()) {
			StringBuilder builder = new StringBuilder();
			builder.append("found: {");
			boolean first = true;
			for(String result : results) {
				if(!first) {
					builder.append(", ");
				}
				first = false;
				builder.append(result);
				
			}
			builder.append("}");
			System.out.println(builder.toString());
		}
		Assert.assertEquals(expected, results.size());
		if(match != null && match.length > 0) {
			List<String> listOne = Arrays.asList(match);
			List<String> listTwo = new ArrayList<String>(results);
			
			Collections.sort(listOne);
			Collections.sort(listTwo);
			
			Assert.assertEquals(listOne, listTwo);
		}
	}
	
}
