package com.achoo.topicstore.trie;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;

public class TerminationNode extends AbstractNode {

	static final char TERMINATED = '\0';
	
	TerminationNode(Node parent) {
		super(parent);		
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
	public Map<Character, Node> children() {
		return Collections.emptyMap();
	}

	@Override
	protected void putChild(char key, Node value) {
		return;
	}

	@Override
	public void merge(Node node) {
		return;
	}

	@Override
	public Set<Node> find(String input, boolean exact) {
		// only returns the termination node if the
		// string was empty (so termination has to 
		// land on a termination node)
		LinkedHashSet<Node> results = new LinkedHashSet<>();
		if(Strings.isNullOrEmpty(input)) {
			results.add(this.parent());
		} 
		return results;
	}

	@Override
	public Set<String> paths() {
		return Collections.emptySet();
	}
}
