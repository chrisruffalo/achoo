package com.achoo.topicstore.trie;

import java.util.Map;
import java.util.Set;

public interface Node {

	char value();
	
	Set<Node> find(String input);
	
	Set<Node> find(String input, boolean exact);
	
	void find(Set<Node> destination, String input, int index, boolean exact);
	
	Map<Character, Node> children();
	
	void merge(Node node);
	
	boolean matches(char input);
	
	boolean matches(char input, boolean exact);
	
	Set<String> paths();
	
	String name();
	
	RootNode root();
	
	Node parent();
}
