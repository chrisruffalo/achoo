package com.achoo.topicstore.trie;

import java.util.LinkedHashSet;
import java.util.Set;

public class RootNode extends AbstractNode {

	static final char ROOT = '\0';
	
	RootNode() {
		super(null);
	}
	
	@Override
	public char value() {
		return RootNode.ROOT;
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
			
			// update root and parent
			if(node instanceof AbstractNode) {
				AbstractNode abstractNode = (AbstractNode)node;
				abstractNode.root(this.root());
				abstractNode.parent(this);
			}
		}
	}

	@Override
	public void find(Set<Node> destination, String input, int index, boolean exact) {
		this.childFind(destination, input, index, exact);
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

	@Override
	public RootNode root() {
		return this;
	}

	@Override
	public Node parent() {
		return this;
	}
	
	@Override
	public String name() {
		return "";
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [children='" + this.children().size() + "']";
	}

}
