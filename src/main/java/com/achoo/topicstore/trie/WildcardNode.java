package com.achoo.topicstore.trie;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.base.Strings;


public class WildcardNode extends AbstractNode {

	static final char WILDCARD = '*';
	
	WildcardNode(Node parent) {
		super(parent);
	}
	
	@Override
	public char value() {
		return WildcardNode.WILDCARD;
	}
	
	@Override
	public boolean matches(char input, boolean exact) {
		if(exact) {
			return Character.compare(input, this.value()) == 0;
		}
		return true;
	}

	@Override
	public Set<Node> find(String input, boolean exact) {
		if(Strings.isNullOrEmpty(input)) {
			Node termination = this.children().get(TerminationNode.TERMINATED);
			if(termination != null) {
				return termination.find(input);
			}
			return new LinkedHashSet<>();
		}
		
		if(this.children().size() == 1) {
			Node termination = this.children().get(TerminationNode.TERMINATED);
			if(termination != null) {
				return termination.find("");
			}
		}
		
		Set<Node> results = null;
		do {
			if(results == null) {
				results = super.find(input, exact);
			} else {
				results.addAll(super.find(input, exact));
			}
			input = input.substring(1);
		} while(!Strings.isNullOrEmpty(input));
		
		return results;
	}
	
	
}
