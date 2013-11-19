package com.achoo.topicstore.tst;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public abstract class DepthContentNode<D> extends AbstractNode<D> {

	private Map<Integer, Set<D>> depthMap;
	
	protected Set<D> getContentAtDepth(int depth) {
		if(this.depthMap == null || this.depthMap.isEmpty()) {
			return Collections.emptySet();
		}
		Set<D> content = this.depthMap.get(depth);
		if(content == null || content.isEmpty()) {
			return Collections.emptySet();
		}
		return content;
	}
	
	protected void addContentAtDepth(int depth, Collection<D> values) {
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
	
}
