package com.achoo.topicstore.trie;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class PartitionMap implements Map<Character, Node> {

	private static final int BLOCK_SIZE = Character.MAX_CODE_POINT;
	
	private final Long start;
	
	private final Map<Long, Node> backer;
	
	private AtomicInteger size;
	
	private Integer lowest;
	
	private Integer highest;
	
	private class PartitionKey implements Entry<Character, Node> {

		private Character key;
		
		private Node value;
		
		public PartitionKey(Character character, Node node) {
			this.key = character;
			this.value = node;
		}
		
		@Override
		public Character getKey() {
			return this.key;
		}

		@Override
		public Node getValue() {
			return this.value;
		}

		@Override
		public Node setValue(Node arg0) {
			this.value = arg0;
			return this.value;
		}
	}
	
	public PartitionMap(int partition, Map<Long, Node> backer) {
		if(partition < 0) {
			throw new IllegalArgumentException("Partition cannot be created with a partition less than 0");
		}
		
		this.start = Long.valueOf(partition * PartitionMap.BLOCK_SIZE);
		this.lowest = Integer.valueOf(PartitionMap.BLOCK_SIZE);
		this.highest = Integer.valueOf(0);
		this.backer = backer;
		this.size = new AtomicInteger(0);
	}
	
	@Override
	public void clear() {
		Set<Character> keys = this.keySet();
		for(Character key : keys) {
			this.backer.remove(this.partitionKey(key));
		}
		this.size.set(0);
	}
	
	private Long partitionKey(Object input) {
		if(input == null) {
			// need to throw exception here
			return Long.valueOf(0);
		}
		int base = 0;
				
		if(input instanceof Node) {
			Node iNode = (Node)input;
			base = Character.valueOf(iNode.value()).hashCode();
		} else if(input instanceof Character) {
			base = Character.valueOf((char)input).hashCode();
		} else {
			base = input.hashCode();
		}

		// finish calculations
		base += this.start;
		return Long.valueOf(base);
	}

	@Override
	public boolean containsKey(Object key) {
		return this.backer.containsKey(this.partitionKey(key));
	}

	@Override
	public boolean containsValue(Object value) {
		return this.backer.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<Character, Node>> entrySet() {
		Set<Entry<Character,Node>> values = new HashSet<>();
		for(long i = (this.start + this.lowest); i <= (this.start + this.highest); i++) {
			Node value = this.backer.get(i);
			if(value != null) {
				values.add(new PartitionKey(value.value(), value));
			}
		}		
		return values;
	}

	@Override
	public Node get(Object key) {
		long keyValue = this.partitionKey(key);
		return this.backer.get(keyValue);
	}

	@Override
	public boolean isEmpty() {
		return 0 == this.size.get();
	}

	@Override
	public Set<Character> keySet() {
		Set<Character> values = new HashSet<>();
		for(Entry<Character, Node> entry : this.entrySet()) {
			values.add(entry.getKey());
		}
		return values;
	}

	@Override
	public Node put(Character arg0, Node arg1) {
		if(arg1 == null) {
			return null;
		}
		long key = this.partitionKey(arg0);
		int adjusted = (int)(key - this.start);
		if(adjusted < this.lowest) {
			this.lowest = Integer.valueOf(adjusted);
		}
		if(adjusted > this.highest) {
			this.highest = Integer.valueOf(adjusted);
		}
		this.size.incrementAndGet();
		return this.backer.put(key, arg1);
	}

	@Override
	public void putAll(Map<? extends Character, ? extends Node> arg0) {
		for(Entry<? extends Character,? extends Node> entry : arg0.entrySet()) {
			Node value = entry.getValue();
			this.backer.put(this.partitionKey(value), value);
		}
	}

	@Override
	public Node remove(Object arg0) {
		this.size.decrementAndGet();
		return this.backer.remove(this.partitionKey(arg0));
	}

	@Override
	public int size() {
		return this.size.get();
	}

	@Override
	public Collection<Node> values() {
		Set<Node> values = new HashSet<>();
		for(Entry<Character, Node> entry : this.entrySet()) {
			values.add(entry.getValue());
		}
		return values;

	}

}
