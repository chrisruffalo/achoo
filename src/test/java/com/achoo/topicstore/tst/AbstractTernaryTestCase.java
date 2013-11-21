package com.achoo.topicstore.tst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;

public abstract class AbstractTernaryTestCase {

	public void check(SearchNode<String> tree, int expected, String key, boolean exact, String... match) {
		Set<String> actualResults = tree.find(key, exact);
		List<String> sortedResults = new ArrayList<String>(actualResults);
		Collections.sort(sortedResults);
		
		if(expected != actualResults.size()) {
			StringBuilder builder = new StringBuilder();
			builder.append("found: {");
			boolean first = true;
			for(String result : sortedResults) {
				if(!first) {
					builder.append(", ");
				}
				first = false;
				builder.append(result);
				
			}
			builder.append("}");
			System.out.println(builder.toString());
		}
		Assert.assertEquals(expected, actualResults.size());
		if(match != null && match.length > 0) {
			List<String> expectedResults = Arrays.asList(match);
			Collections.sort(expectedResults);
			
			Assert.assertEquals(expectedResults, sortedResults);
		}
	}
	
}
