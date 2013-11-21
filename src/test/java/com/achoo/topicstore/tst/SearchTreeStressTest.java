package com.achoo.topicstore.tst;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class SearchTreeStressTest {

	// in percent
	private static final int LOG_INTERVAL = 10;
	
	private static final int SIZE = 10000;
	
	private static final int LENGTH = 50;
	
	@Test
	public void stressWithWildcards() {
		Logger logger = LoggerFactory.getLogger(this.getClass()); 
		
		SearchTree<String> node = new SearchTree<>();
		node.add(Strings.repeat("#", SearchTreeStressTest.LENGTH), "root-max-any-character");
		
		long start = System.currentTimeMillis();
		long nextPercent = SearchTreeStressTest.SIZE / SearchTreeStressTest.LOG_INTERVAL;
		for(long i = 0; i < SearchTreeStressTest.SIZE; i++) {
			String generated = this.generate(true);
			Assert.assertEquals(SearchTreeStressTest.LENGTH, generated.length());
			//System.out.println("generated: " + generated);
			node.add(generated, new String(generated.substring(0, 1)));
			
			if(i > nextPercent) {
				logger.info("generated {} (of {}) items in : {}ms", new Object[]{i, SearchTreeStressTest.SIZE, (System.currentTimeMillis() - start)});
				nextPercent = nextPercent + (SearchTreeStressTest.SIZE / SearchTreeStressTest.LOG_INTERVAL);	
			}
		}
		node.add(Strings.repeat("#", SearchTreeStressTest.LENGTH), "max-any-character");

		logger.info("generation took: " + (System.currentTimeMillis() - start) + "ms");
		start = System.currentTimeMillis();
		
		// print for debug of small sets, printing large sets is a bad idea
		//node.print();
		
		Set<String> findSet = new LinkedHashSet<>();
		nextPercent = SearchTreeStressTest.SIZE / SearchTreeStressTest.LOG_INTERVAL;
		for(long i = 0; i < SearchTreeStressTest.SIZE; i++) {
			String generated = this.generate(false);
			Assert.assertEquals(SearchTreeStressTest.LENGTH, generated.length());
			//logger.info("searching: {}", generated);
			node.lookup(findSet, generated, false);
						
			if(i > nextPercent) {
				logger.info("searched {} (of {}) items in : {}ms", new Object[]{i, SearchTreeStressTest.SIZE, (System.currentTimeMillis() - start)});
				nextPercent = nextPercent + (SearchTreeStressTest.SIZE / SearchTreeStressTest.LOG_INTERVAL);	
			}
		}
		
		double operations = (long)Math.pow(SearchTreeStressTest.SIZE, 2);
		long delta = (System.currentTimeMillis() - start);
		double deltaSeconds = (delta*1.0d) / 1000.0d;
		double operationsPerSecond = operations/deltaSeconds;
		logger.info("searching took: {}ms ({} comparisons at {} comparisons per second)", delta, operations, operationsPerSecond);
		logger.info("found: " + findSet.size() + " nodes");
		
		for(String found : findSet) {
			logger.info("\t{}", found);
		}
	}
	
	private String generate(boolean wildcards) {
		// choose random prefix
		StringBuilder builder = new StringBuilder("");
		
		// a-z, 0-9, with optional # and *
		int max = (wildcards) ? 38 : 36;
		
		for(int i = 0; i < LENGTH; i++) {
			double iRandom = Math.random();
			int iChar = (int)(iRandom * max); 
			switch(iChar) {
				case 37:
					builder.append("#");
					break;
				case 36:
					builder.append("*");
					break;
				default:
					if(iChar < 26) {
						builder.append(Character.toString((char)(iChar + 'a')));
					} else {
						builder.append(Character.toString((char)(iChar + '0')));
					}					
					break;
			}
		}
		return builder.toString();
	}
	
}
