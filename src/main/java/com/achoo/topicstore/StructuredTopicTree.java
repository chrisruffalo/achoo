package com.achoo.topicstore;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;

public class StructuredTopicTree implements StructuredTopic {

	private String uuid;
	
	private String part;
	
	private long hash;	
	
	private Map<Long, StructuredTopic> children;
	
	private StructuredTopic parent;
	
	private StructuredTopicTree(String uuid, String part) {
		this.uuid = uuid;
		this.part = part;
		this.children = new HashMap<>(5);
		this.parent = null;
	}
	
	public String uuid() {
		return this.uuid;
	}
	
	public void add(StructuredTopic tree) {
		// do not add null, empty, or bad tree part
		if(tree == null || Strings.isNullOrEmpty(tree.part())) {
			return;
		}
		
		// create relationships
		this.children.put(tree.hash(), tree);
		
		if(tree instanceof StructuredTopicTree) {
			((StructuredTopicTree)tree).parent = this;
		}
	}
	
	public List<StructuredTopicTree> search(String topicString) {
		return Collections.emptyList();
	}

	@Override
	public boolean matchesPart(String topic) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public StructuredTopic parent() {
		return this.parent;
	}

	@Override
	public long hash() {
		return this.hash;
	}

	@Override
	public String part() {
		return this.part;
	}

	@Override
	public Collection<StructuredTopic> children() {
		return Collections.unmodifiableCollection(this.children.values());
	}
	
}
