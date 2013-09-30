package com.achoo.topicstore.trie;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.achoo.exceptions.AchooRuntimeException;
import com.googlecode.javaewah32.EWAHCompressedBitmap32;

public class PartitionMap implements Map<Character, Node> {

	private static final int BLOCK_SIZE = Character.MAX_CODE_POINT;
	
	private final int partition;
	
	private final Map<Long, Node> backer;
	
	private EWAHCompressedBitmap32 marker;
	
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
		
		this.partition = partition;
		this.backer = backer;
		this.marker = new EWAHCompressedBitmap32();
	}
	
	@Override
	public void clear() {
		Set<Character> keys = this.keySet();
		for(Character key : keys) {
			this.backer.remove(this.partitionKey(key));
		}
		this.marker.clear();
	}
	
	private Long partitionKey(Object input) {
		if(input == null) {
			// need to throw exception here
			return Long.valueOf(0);
		}
		long base = 0;
				
		if(input instanceof Node) {
			Node iNode = (Node)input;
			base = Character.valueOf(iNode.value()).hashCode();
		} else if(input instanceof Character) {
			base = Character.valueOf((char)input).hashCode();
		} else {
			base = input.hashCode();
		}

		// finish calculations
		base += (((long)this.partition) * PartitionMap.BLOCK_SIZE);
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
		long start = ((long)this.partition) * PartitionMap.BLOCK_SIZE;

		for(int i : this.marker.toArray()) {
			long position = start + i;
			Node node = this.backer.get(position);
			if(node != null) {
				values.add(new PartitionKey(node.value(), node));
			}			
		}
	
		if(values.size() != this.size()) {
			// bad juju
			throw new AchooRuntimeException("value size of " + values.size() + " does not match calculated partition size of " + this.size());
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
		return this.marker.equals(BigInteger.ZERO);
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
		int adjusted = (int)(key - (((long)this.partition) * PartitionMap.BLOCK_SIZE));
		EWAHCompressedBitmap32 given = new EWAHCompressedBitmap32();
		given.set(adjusted);
		this.marker = this.marker.or(given);
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
		long key = this.partitionKey(arg0);
		int adjusted = (int)(key - (((long)this.partition) * PartitionMap.BLOCK_SIZE));
		EWAHCompressedBitmap32 inverse = new EWAHCompressedBitmap32();
		inverse.set(adjusted);
		this.marker = this.marker.andNot(inverse);
		return this.backer.remove(key);
	}

	@Override
	public int size() {
		return this.marker.toArray().length;
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
