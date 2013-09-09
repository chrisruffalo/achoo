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
	public Node parent() {
		return this;
	}
	
	public Node root() {
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
