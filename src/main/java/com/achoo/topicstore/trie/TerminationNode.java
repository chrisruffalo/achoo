package com.achoo.topicstore.trie;

import java.util.Collections;
import java.util.Set;

public class TerminationNode extends AbstractNode {

	static final char TERMINATED = '\0';
	
	TerminationNode(Node parent) {
		super(parent, null);		
	}
	
	@Override
	public char value() {
		return TerminationNode.TERMINATED;
	}

	@Override
	public boolean matches(char input, boolean exact) {
		return false;
	}

	@Override
	public Set<Node> children() {
		return Collections.emptySet();
	}

	@Override
	public void merge(Node node) {
		return;
	}

	@Override
	public void find(Set<Node> destination, String input, int index, boolean exact) {
		// only returns the termination node if the
		// string was empty (so termination has to 
		// land on a termination node)
		if(index >= input.length()) {
			destination.add(this.parent());
		} 
	}
	
	@Override
	public String name() {
		return this.parent().name();
	}

	@Override
	public Set<String> paths() {
		// termination nodes are the only nodes that actually ever
		// return anything so that it rolls up back the tree
		return Collections.singleton(this.name());
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [name='" + this.name() + "', value='TERMINATION']";
	}

}