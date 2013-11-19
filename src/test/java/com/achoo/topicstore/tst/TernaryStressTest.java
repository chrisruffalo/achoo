package com.achoo.topicstore.tst;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class TernaryStressTest {

	private static final int LOG_INTERVAL = 10;
	
	private static final int SIZE = 2000000;
	
	private static final int LENGTH = 30;
	
	@Test
	public void stressWithWildcards() {
		Logger logger = LoggerFactory.getLogger(this.getClass()); 
		
		SearchTree<String> node = new SearchTree<>();
		node.add(Strings.repeat("#", TernaryStressTest.LENGTH), "root-max-any-character");
		
		long start = System.currentTimeMillis();
		long nextPercent = TernaryStressTest.SIZE / TernaryStressTest.LOG_INTERVAL;
		for(long i = 0; i < TernaryStressTest.SIZE; i++) {
			String generated = this.generate(true);
			Assert.assertEquals(TernaryStressTest.LENGTH, generated.length());
			//System.out.println("generated: " + generated);
			node.add(generated, new String(generated.substring(0, 1)));
			
			if(i > nextPercent) {
				logger.info("generated {} (of {}) items in : {}ms", new Object[]{i, TernaryStressTest.SIZE, (System.currentTimeMillis() - start)});
				nextPercent = nextPercent + (TernaryStressTest.SIZE / TernaryStressTest.LOG_INTERVAL);	
			}
		}
		node.add(Strings.repeat("#", TernaryStressTest.LENGTH), "max-any-character");

		logger.info("generation took: " + (System.currentTimeMillis() - start) + "ms");
		start = System.currentTimeMillis();
		
		// print for debug of small sets, printing large sets is a bad idea
		//node.print();
		
		Set<String> findSet = new LinkedHashSet<>();
		nextPercent = TernaryStressTest.SIZE / TernaryStressTest.LOG_INTERVAL;
		for(long i = 0; i < TernaryStressTest.SIZE; i++) {
			String generated = this.generate(false);
			Assert.assertEquals(TernaryStressTest.LENGTH, generated.length());
			//logger.info("searching: {}", generated);
			node.lookup(findSet, generated, false);
						
			if(i > nextPercent) {
				logger.info("searched {} (of {}) items in : {}ms", new Object[]{i, TernaryStressTest.SIZE, (System.currentTimeMillis() - start)});
				nextPercent = nextPercent + (TernaryStressTest.SIZE / TernaryStressTest.LOG_INTERVAL);	
			}
		}
		
		logger.info("searching took: " + (System.currentTimeMillis() - start) + "ms");
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
