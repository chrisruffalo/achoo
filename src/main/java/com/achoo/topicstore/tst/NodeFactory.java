package com.achoo.topicstore.tst;

public final class NodeFactory {

	private NodeFactory() {
		// cannot construct
	}
	
	static <D> InternalNode<D> create(Character local) {
		
		final InternalNode<D> node;
		if(AnyCharacterNode.ANY_CHARACTER.equals(local)) {
			node = new AnyCharacterNode<>();
		} else {
			node = new LiteralNode<>(local);
		}
		
		return node;
	}
}
