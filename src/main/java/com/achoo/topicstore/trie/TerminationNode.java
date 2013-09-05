package com.achoo.topicstore.trie;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;

public class TerminationNode extends AbstractNode {

	private static final char TERMINATED = '\0';
	
	private final Node terminated; 
	
	TerminationNode(Node terminated) {
		super();
		
		this.terminated = terminated;
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
		if(Strings.isNullOrEmpty(input)) {
			return Collections.singleton(this.terminated);
		} else {
			return Collections.emptySet();
		}
	}

	@Override
	public Set<String> paths() {
		return Collections.emptySet();
	}
	
	

}
