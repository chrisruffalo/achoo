package com.achoo.topicstore.tst;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.achoo.topicstore.tst.visitor.AddingVisitor;
import com.achoo.topicstore.tst.visitor.SearchingVisitor;

public abstract class AbstractNode<D> implements InternalNode<D> {

	private Map<Integer, Set<D>> depthMap;
	
	public AbstractNode() {

	}

	@Override
	public Set<D> find(String key, boolean exact) {
		SearchingVisitor<D> visitor = new SearchingVisitor<>();
		this.visit(visitor, key.toCharArray(), 0, exact);
		Set<D> results = visitor.found();
		return results;
	}

	@Override
	public void put(String key, D value) {
		this.put(key, Collections.singleton(value));
	}

	@Override
	public void put(String key, D[] values) {
		if(values == null || values.length == 0) {
			return;
		}
		this.put(key, Arrays.asList(values));
	}

	@Override
	public void put(String key, Collection<D> values) {
		if(values == null || values.isEmpty()) {
			return;
		}
		
		if(key == null || key.isEmpty()) {
			return;
		}
		
		AddingVisitor<D> addingVisitor = new AddingVisitor<>(values);
		this.visit(addingVisitor, key.toCharArray(), 0, true);
	}
	
	@Override
	public Set<D> get(int depth) {
		if(this.depthMap == null || this.depthMap.isEmpty()) {
			return Collections.emptySet();
		}
		Set<D> content = this.depthMap.get(depth);
		if(content == null || content.isEmpty()) {
			return Collections.emptySet();
		}
		return content;
	}
	
	public void add(int depth, Collection<D> values) {
		if(values == null || values.isEmpty()) {
			return;
		}
		if(this.depthMap == null || this.depthMap.isEmpty()) {
			this.depthMap = new TreeMap<>(); 
		}
		Set<D> content = this.depthMap.get(depth);
		if(content == null || content.isEmpty()) {
			content = new TreeSet<>();
			this.depthMap.put(depth, content);
		}
		for(D value : values) {
			if(value != null) {
				content.add(value);
			}
		}
	}
	
	protected String contentString() {
		if(this.depthMap == null || this.depthMap.isEmpty()) {
			return "{}";
		}
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for(Map.Entry<Integer, Set<D>> entry : this.depthMap.entrySet()) {
			Integer key = entry.getKey();
			Set<D> set = entry.getValue();
			if(set == null || set.isEmpty()) {
				continue;
			}
			if(!first) {
				builder.append(", ");
			}
			first = false;
			builder.append(key);
			builder.append(":{");
			boolean innerFirst = true;
			for(D value : set) {
				if(value == null) {
					continue;
				}
				String printString = String.valueOf(value);
				if(printString == null || printString.isEmpty()) {
					continue;
				}
				if(!innerFirst) {
					builder.append(", ");
				}
				innerFirst = false;
				builder.append(printString);
			}
			builder.append("}");
		}
		
		return builder.toString();
	}
	
	public void print() {
        print("", "", true);
    }

}
