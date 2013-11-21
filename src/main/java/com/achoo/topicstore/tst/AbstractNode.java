package com.achoo.topicstore.tst;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.achoo.topicstore.tst.config.SearchConfiguration;
import com.achoo.topicstore.tst.visitor.AddingVisitor;
import com.achoo.topicstore.tst.visitor.SearchingVisitor;

public abstract class AbstractNode<D> implements InternalNode<D> {

	private Set<D> values;
	
	private SearchConfiguration configuration;
	
	public AbstractNode(SearchConfiguration configuration) {
		this.configuration = configuration;
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
		if(this.values == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(this.values);
	}
	
	public void add(int depth, Collection<D> values) {
		if(values == null || values.isEmpty()) {
			return;
		}
		if(this.values == null) {
			this.values = new TreeSet<>();
		}
		for(D value : values) {
			if(value != null) {
				this.values.add(value);
			}
		}
	}
	
	protected String contentString() {
		if(this.values == null || this.values.isEmpty()) {
			return "{}";
		}
		StringBuilder builder = new StringBuilder("{");
		boolean first = true;
		for(D value : this.values) {
			if(value == null) {
				continue;
			}
			String printString = String.valueOf(value);
			if(printString == null || printString.isEmpty()) {
				continue;
			}
			if(!first) {
				builder.append(", ");
			}
			first = false;
			builder.append(printString);
		}
		builder.append("}");		
		return builder.toString();
	}
	
	protected InternalNode<D> construct(Character local) {
		return NodeFactory.create(this, local, this.configuration);
	}
	
	public void print() {
        print("", "", true);
    }

}
