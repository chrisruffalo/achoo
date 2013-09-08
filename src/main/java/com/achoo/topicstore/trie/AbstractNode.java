package com.achoo.topicstore.trie;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;

public abstract class AbstractNode implements Node {
	
	private final Map<Character, Node> children;
	
	//private final Logger logger;
	
	private RootNode root;
	
	private Node parent;
	
	AbstractNode(Node parent) {
		this.parent = parent;
		//this.logger = LoggerFactory.getLogger(this.getClass());
		this.children = new LinkedHashMap<>();
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
		if(node instanceof RootNode) {
			throw new InvalidMergeException("A root node cannot be merged into a non-root node");
		}
		
		if(node == null) {
			throw new InvalidMergeException("A null node cannot be merged");
		}
		
		//this.logger.trace("Merging {} into {}", this, node);
		
		Node localChild = this.children.get(node.value());
		if(localChild != null) {
			for(Node remoteChild : node.children().values()) {
				localChild.merge(remoteChild);
			}
			//this.logger.trace("Merging '{}' into node for '{}'", node.value(), this.value());
		} else {
			this.children.put(node.value(), node);
			//this.logger.trace("Putting '{}' into node for '{}'", node.value(), this.value());
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
		//this.logger.trace("getting paths from {}", this);
		
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
	
	public String name() {
		return this.parent.name() + this.value();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [value='" + this.value() + "', children='" + this.children.size() + "']";
	}
	
}
