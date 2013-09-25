package com.achoo.topicstore.trie;

import java.util.Collection;
import java.util.Set;

public interface Node {

	char value();
	
	Set<Node> find(String input);
	
	Set<Node> find(String input, boolean exact);
	
	void find(Set<Node> destination, String input, int index, boolean exact);
	
	Collection<Node> children();
	
	void merge(Node node);
	
	void merge(Node... nodes);
	
	void merge(Collection<Node> nodes);
	
	boolean matches(char input);
	
	boolean matches(char input, boolean exact);
	
	Set<String> paths();
	
	String name();
	
	Node parent();
	
	void parent(Node parent);
	
	Node root();
}
