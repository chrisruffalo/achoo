package com.achoo.topicstore.trie;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;

public abstract class AbstractNode implements Node {
	
	private final Map<Character, Node> children;

	private Node parent;
	
	private String cachedName;
	
	AbstractNode(Node parent) {
		this.parent = parent;
		this.children = new LinkedHashMap<>();
		this.cachedName = null;
	}
	
	@Override
	public Map<Character, Node> children() {
		return this.children;
	}
	
	protected void putChild(char key, Node value) {
		this.children.put(key, value);
	}

	@Override
	public void merge(Node node) {
		if(node == null) {
			throw new InvalidMergeException("A null node cannot be merged");
		}

		if(node instanceof RootNode) {
			this.merge(node.children().values());
			return;
		}
	
		// update child if it exists
		Node localChild = this.children.get(node.value());
		if(localChild != null) {
			localChild.merge(node.children().values());
		} else {
			// otherwise insert child directly
			this.children.put(node.value(), node);
			node.parent(this);
		}
	}
	
	public void merge(Node... nodes) {
		this.merge(Arrays.asList(nodes));
	}
	
	public void merge(Collection<Node> nodes) {
		for(Node node : nodes) {
			this.merge(node);
		}
	}

	@Override
	public Set<Node> find(String input) {
		return this.find(input, false);
	}

	@Override
	public Set<Node> find(String input, boolean exact) {
		Set<Node> destination = new LinkedHashSet<>();
		this.find(destination, input, 0, exact);
		return destination;
	}
	
	@Override
	public void find(Set<Node> destination, String input, int index, boolean exact) {
		//this.logger.trace("Searching for : '{}' on node '{}'", input, this);
		
		if(index > input.length()) {
			this.checkAndFind(TerminationNode.TERMINATED, destination, input, index, exact);
			return;
		}

		if(index < input.length()) {
			char head = input.charAt(index);
			if(this.matches(head, exact)) {
				index++;
				this.childFind(destination, input, index, exact);
			}
		}		 
	}
	
	protected void childFind(Set<Node> destination, String input, int index, boolean exact) {
		if(index < input.length()) {
			char head = input.charAt(index);
			this.checkAndFind(head, destination, input, index, exact);
		}		
	
		this.checkAndFind(TerminationNode.TERMINATED, destination, input, index, exact);
		
		if(!exact) {
			this.checkAndFind(AnyCharacterNode.ANYCHARACTER, destination, input, index, exact);
			this.checkAndFind(WildcardNode.WILDCARD, destination, input, index, exact);
		}
	}
	
	private boolean checkAndFind(char key, Set<Node> destination, String input, int index, boolean exact) {
		Node testNode = this.children.get(key);
		if(testNode != null) {
			testNode.find(destination, input, index, exact);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean matches(char input) {
		return this.matches(input, false);
	}
	
	@Override
	public Set<String> paths() {
		Set<String> paths = new LinkedHashSet<>();
		for(Node child : this.children().values()) {
			for(String path : child.paths()) {
				if(!Strings.isNullOrEmpty(path)) {
					paths.add(path);
				}
			}
		}
		
		return Collections.unmodifiableSet(paths);
	}
	
	public void parent(Node parent) {
		this.parent = parent;
		this.cachedName = null;
	}
	
	public Node parent() {
		return this.parent;
	}
	
	private String calculateName() {
		if(this.parent != null) {
			return this.parent.name() + this.value();
		}
		return ""+this.value();
	}
	
	public String name() {
		if(this.cachedName == null) {
			this.cachedName = this.calculateName();
		}		
		return this.cachedName;
	}
	
	public Node root() {
		return this.parent.root();
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [name='" + this.name() + "', value='" + this.value() + "', children='" + this.children.size() + "']";
	}
	
}
