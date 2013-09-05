package com.achoo.topicstore.trie;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class RootNode extends AbstractNode {

	private static final char EMPTY = '\0';
	
	@Override
	public char value() {
		return RootNode.EMPTY;
	}
	
	@Override
	public void merge(Node node) {
		
		// handle even-steven merge of root nodes
		if(node instanceof RootNode) {
			for(Node rootChild : node.children().values()) {
				this.merge(rootChild);
			}
			return;
		}
		
		// otherwise manually merge into this (root) node
		if(this.children().containsKey(node.value())) {
			Node localChild = this.children().get(node.value());
			if(localChild != null) {
				for(Node remoteChild : node.children().values()) {
					localChild.merge(remoteChild);
				}
			}
		} else {
			this.putChild(node.value(), node);
		}
	}
	
	

	@Override
	public Set<Node> find(String input, boolean exact) {
		Set<Node> results = new LinkedHashSet<>();
		for(Node node : this.children().values()) {
			results.addAll(node.find(input, exact));
		}
		return Collections.unmodifiableSet(results);
	}

	@Override
	public boolean matches(char input, boolean exact) {
		// root node always matches (for proper handling)
		return true;
	}

	@Override
	public Set<String> paths() {
		Set<String> paths = new LinkedHashSet<>();
		for(Node child : this.children().values()) {
			paths.addAll(child.paths());
		}
		return paths;
	}

	
}
