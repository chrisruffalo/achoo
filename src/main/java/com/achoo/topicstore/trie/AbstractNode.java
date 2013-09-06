package com.achoo.topicstore.trie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public abstract class AbstractNode implements Node {
	
	private Map<Character, Node> children;
	
	private Logger logger;
	
	private RootNode root;
	
	private Node parent;
	
	AbstractNode(Node parent) {
		this.parent = parent;
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
		
		this.logger.trace("Merging {} into {}", this, node);
		
		Node localChild = this.children.get(node.value());
		if(localChild != null) {
			for(Node remoteChild : node.children().values()) {
				localChild.merge(remoteChild);
			}
			this.logger.trace("Merging '{}' into node for '{}'", node.value(), this.value());
		} else {
			this.children.put(node.value(), node);
			this.logger.trace("Putting '{}' into node for '{}'", node.value(), this.value());
		}
		
		// update root and parent
		if(node instanceof AbstractNode) {
			AbstractNode abstractNode = (AbstractNode)node;
			abstractNode.root(this.root());
			abstractNode.parent(this);
		}		
	}

	@Override
	public Set<Node> find(String input) {
		return this.find(input, false);
	}

	@Override
	public Set<Node> find(String input, boolean exact) {
		this.logger.trace("Searching for : '{}' on node '{}'", input, this);
		
		if(Strings.isNullOrEmpty(input)) {
			Node termination = this.children.get(TerminationNode.TERMINATED);
			if(termination != null) {
				return termination.find(input);
			}
			return new LinkedHashSet<>();
		}
		
		char head = input.charAt(0);
		if(input.length() == 1) {
			input = "";
		} else {
			input = input.substring(1);
		}
		 
		if(this.matches(head, exact)) {
			return this.childFind(input, exact);
		} else {
			return new LinkedHashSet<>();
		}
	}
	
	protected Set<Node> childFind(String input, boolean exact) {
		Set<Node> nodes = null;
		
		if(!input.isEmpty()) {
			char head = input.charAt(0);
			
			Node childFind = this.children.get(head);
			if(childFind != null) {
				nodes = childFind.find(input);
			}
		}
		
		if(nodes == null) {
			nodes = new LinkedHashSet<>();
		}
					
		List<Character> additional = new ArrayList<>(((exact) ? 1 : 3));
		additional.add(TerminationNode.TERMINATED);
		if(!exact) {
			additional.add(WildcardNode.WILDCARD);
			additional.add(AnyCharacterNode.ANYCHARACTER);
		}
		
		for(Character test : additional) {
			Node testNode = this.children.get(test);
			if(testNode != null) {
				nodes.addAll(testNode.find(input));
			}
		}
		
		return nodes;
	}
	
	@Override
	public boolean matches(char input) {
		return this.matches(input, false);
	}
	
	@Override
	public Set<String> paths() {
		this.logger.trace("getting paths from {}", this);
		
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
	
	void parent(Node parent) {
		this.parent = parent;
	}
	
	public Node parent() {
		return this.parent;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [value='" + this.value() + "', children='" + this.children.size() + "']";
	}
	
}
