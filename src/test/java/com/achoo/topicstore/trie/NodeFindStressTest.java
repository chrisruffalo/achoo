package com.achoo.topicstore.trie;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class NodeFindStressTest {

	private static final int SIZE = 100;
	
	private static final int LENGTH = 160;
	
	@Test
	public void stressWithWildcards() {
		Logger logger = LoggerFactory.getLogger(this.getClass()); 
		
		Node node = NodeFactory.generate("*");
		long start = System.currentTimeMillis();
		for(long i = 0; i < NodeFindStressTest.SIZE; i++) {
			String generated = this.generate(true);
			Assert.assertEquals(NodeFindStressTest.LENGTH, generated.length());
			//System.out.println("generated: " + generated);
			node.merge(NodeFactory.generate(generated));
		}
		node.merge(NodeFactory.generate(Strings.repeat("#", NodeFindStressTest.LENGTH)));

		logger.info("generation took: " + (System.currentTimeMillis() - start) + "ms");
		start = System.currentTimeMillis();
		
		Set<Node> findSet = new LinkedHashSet<>();
		long nextPercent = NodeFindStressTest.SIZE / 10;
		for(long i = 0; i < NodeFindStressTest.SIZE; i++) {
			String generated = this.generate(false);
			Assert.assertEquals(NodeFindStressTest.LENGTH, generated.length());
			//System.out.println("searching: " + generated);
			node.find(findSet, generated, 0, false);
						
			if(i > nextPercent) {
				logger.info("searched {} (of {}) items in : {}ms", new Object[]{i, NodeFindStressTest.SIZE, (System.currentTimeMillis() - start)});
				nextPercent = nextPercent + (NodeFindStressTest.SIZE / 10);	
			}
		}
		
		logger.info("searching took: " + (System.currentTimeMillis() - start) + "ms");
		logger.info("found: " + findSet.size() + " nodes");
	}

	@Test
	public void stringCompareComparisonStressTest() {
		Logger logger = LoggerFactory.getLogger(this.getClass()); 
		
		List<String> stringList = new ArrayList<String>(NodeFindStressTest.SIZE);
		long start = System.currentTimeMillis();
		for(long i = 0; i < NodeFindStressTest.SIZE; i++) {
			String generated = this.generate(false);
			Assert.assertEquals(NodeFindStressTest.LENGTH, generated.length());
			stringList.add(generated);
		}

		logger.info("generation took: " + (System.currentTimeMillis() - start) + "ms");
		start = System.currentTimeMillis();
		
		Set<String> findSet = new LinkedHashSet<>();
		long nextPercent = NodeFindStressTest.SIZE / 10;
		for(long i = 0; i < NodeFindStressTest.SIZE; i++) {
			String generated = this.generate(false);
			for(String inner : stringList) {
				if(generated.equals(inner)) {
					findSet.add(generated);
				}
			}						
			if(i > nextPercent) {
				logger.info("searched {} (of {}) items in : {}ms", new Object[]{i, NodeFindStressTest.SIZE, (System.currentTimeMillis() - start)});
				nextPercent = nextPercent + (NodeFindStressTest.SIZE / 10);	
			}
		}
		
		logger.info("searching took: " + (System.currentTimeMillis() - start) + "ms");
		logger.info("found: " + findSet.size() + " nodes");
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
