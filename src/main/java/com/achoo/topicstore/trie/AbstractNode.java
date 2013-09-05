package com.achoo.topicstore.trie;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public abstract class AbstractNode implements Node {
	
	private Map<Character, Node> children;
	
	private Logger logger;
	
	private RootNode root;
	
	AbstractNode() {
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.children = new LinkedHashMap<>(2);
	}
	
	@Override
	public Map<Character, Node> children() {
		return Collections.unmodifiableMap(this.children);
	}
	
	protected void putChild(char key, Node value) {
		this.children.put(key, value);
	}

	@Override
	public void merge(Node node) {
		if(node instanceof RootNode) {
			throw new InvalidMergeException("A root node cannot be merged into a non-root node");
		}
		
		this.logger.trace("Merging {} into {}", this.toString(), node.toString());
		
		if(this.children.containsKey(node.value())) {
			Node localChild = this.children.get(node.value());
			for(Node remoteChild : node.children().values()) {
				localChild.merge(remoteChild);
			}
			this.logger.trace("Merging '{}' into node for '{}'", node.value(), this.value());
		} else {
			this.children.put(node.value(), node);
			this.logger.trace("Putting '{}' into node for '{}'", node.value(), this.value());
		}
	}

	@Override
	public Set<Node> find(String input) {
		return this.find(input, false);
	}

	@Override
	public Set<Node> find(String input, boolean exact) {
		if(Strings.isNullOrEmpty(input)) {
			return Collections.emptySet();
		}
		
		Set<Node> nodes = new LinkedHashSet<>();

		char head = input.charAt(0);
		if(input.length() == 1) {
			input = "";
		} else {
			input = input.substring(1);
		}
		 
		if(this.matches(head, exact)) {
			for(Node child : this.children().values()) {
				nodes.addAll(child.find(input, exact));
			}
		}	
		
		return Collections.unmodifiableSet(nodes);
	}
	
	@Override
	public boolean matches(char input) {
		return this.matches(input, false);
	}
	
	@Override
	public Set<String> paths() {
		this.logger.trace("getting paths from {}", this.toString());
		
		Set<String> paths = new LinkedHashSet<>();
		for(Node child : this.children().values()) {
			for(String path : child.paths()) {
				String local = this.value() + path;
				if(!Strings.isNullOrEmpty(local)) {
					paths.add(local);
				}
			}
		}
		
		String thisNodePath = String.valueOf(this.value()); 
		if(!Strings.isNullOrEmpty(thisNodePath)) {
			paths.add(thisNodePath);
		}
		
		return Collections.unmodifiableSet(paths);
	}
	
	void root(RootNode root) {
		this.root = root;
	}
	
	public RootNode root() {
		return this.root;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [value='" + this.value() + "', children='" + this.children.size() + "']";
	}
	
	
}
