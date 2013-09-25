package com.achoo.topicstore.trie;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class PartitionMapTest {

	private static final int SIZE = 1000;
	
	@Test
	public void testBasicInsert() {
		
		Map<Long, Node> backer = new HashMap<>(PartitionMapTest.SIZE);
		Map<Integer, PartitionMap> selections = new HashMap<>();
		
		for(int i = 1; i <= PartitionMapTest.SIZE; i++) {
			PartitionMap partition = new PartitionMap(i, backer);
			partition.put('a', new LiteralCharacterNode(null, 'a', partition));
			partition.put('e', new LiteralCharacterNode(null, 'e', partition));
			
			Assert.assertEquals(2, partition.size());
			
			selections.put(i, partition);
		}
		
		// assert that two things were imported per round
		Assert.assertEquals(PartitionMapTest.SIZE * 2, backer.size()); 
		
		// verify values
		for(int i = 1; i <= PartitionMapTest.SIZE; i++) {
			PartitionMap partition = selections.get(i);
			
			Assert.assertEquals(2, partition.size());
			Node n1 = partition.get('a');
			Assert.assertNotNull(n1);
			Node n2 = partition.get('e');
			Assert.assertNotNull(n2);
			Node n3 = partition.get('d');
			Assert.assertNull(n3);
			
			// same sizes
			Assert.assertEquals(2, partition.entrySet().size());
			Assert.assertEquals(2, partition.keySet().size());
			Assert.assertEquals(2, partition.values().size());			
		}
	}
	
}
