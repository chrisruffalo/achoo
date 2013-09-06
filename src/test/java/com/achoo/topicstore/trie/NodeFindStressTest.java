package com.achoo.topicstore.trie;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class NodeFindStressTest {

	private static final long SIZE = 750000;
	
	private static final int LENGTH = 20;
	
	@Test
	public void stressWithWildcards() {
		Logger logger = LoggerFactory.getLogger(this.getClass()); 
		
		int found = 0;
		
		Node node = new RootNode();
		long start = System.currentTimeMillis();
		for(long i = 0; i < NodeFindStressTest.SIZE; i++) {
			String generated = this.generate(true);
			Assert.assertEquals(NodeFindStressTest.LENGTH, generated.length());
			//System.out.println("generated: " + generated);
			node.merge(NodeFactory.generate(generated));
		}
		node.merge(NodeFactory.generate("*"));
		node.merge(NodeFactory.generate(Strings.repeat("#", NodeFindStressTest.LENGTH)));

		logger.info("generation took: " + (System.currentTimeMillis() - start) + "ms");
		start = System.currentTimeMillis();
		
		long nextPercent = NodeFindStressTest.SIZE / 10;
		for(long i = 0; i < NodeFindStressTest.SIZE; i++) {
			String generated = this.generate(false);
			Assert.assertEquals(NodeFindStressTest.LENGTH, generated.length());
			//System.out.println("searching: " + generated);
			Set<Node> results = node.find(generated);
			found += results.size();
			
			if(i > nextPercent) {
				logger.info("searched {} (of {}) items in : {}ms", new Object[]{i, NodeFindStressTest.SIZE, (System.currentTimeMillis() - start)});
				nextPercent = nextPercent + (NodeFindStressTest.SIZE / 10);	
			}
		}
		
		logger.info("searching took: " + (System.currentTimeMillis() - start) + "ms");
		logger.info("found: " + found + " nodes");
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
