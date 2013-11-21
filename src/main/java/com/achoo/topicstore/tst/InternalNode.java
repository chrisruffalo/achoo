package com.achoo.topicstore.tst;

import java.util.Collection;
import java.util.Set;

interface InternalNode<D> extends SearchNode<D> {

	void lookup(Set<D> results, char[] key, int index, boolean exact);
	
	void add(char[] key, int index, Collection<D> values);
	
	/**
	 * Checks to see if a search should extend to the node even in the
	 * event that the ternary tree doesn't "lean" that way.  This is
	 * used to "draw" the search to wildcard-type nodes regardless
	 * of the way that the tree lies. 
	 * 
	 * @param exact denotes if the search is exact (no wildcards allowed)
	 * @return true if the search should extend, false otherwise
	 */
	boolean attracts(boolean exact);
	
	/**
	 * Internal mechanism for printing the layout of the nodes
	 * 
	 * @param prefix spacing
	 * @param describe what the node is in relation to the parent
	 * @param isTail if it comes at the end of a block
	 */
	void print(String prefix, String describe, boolean isTail);
}
