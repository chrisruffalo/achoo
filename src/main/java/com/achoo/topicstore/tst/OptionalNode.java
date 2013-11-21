package com.achoo.topicstore.tst;

import java.lang.ref.WeakReference;

import com.achoo.topicstore.tst.config.SearchConfiguration;
import com.achoo.topicstore.tst.matcher.Matcher;
import com.achoo.topicstore.tst.visitor.Visitor;

public class OptionalNode<D> extends AbstractNode<D> {

	final boolean sink;
	
	final private Matcher matcher;
	
	final private WeakReference<InternalNode<D>> parent;
	
	private InternalNode<D> next;
		
	public OptionalNode(InternalNode<D> parent, Matcher matcher, boolean sink, SearchConfiguration configuration) {
		super(configuration);
		this.matcher = matcher;
		if(parent != null) {
			this.parent = new WeakReference<InternalNode<D>>(parent);
		} else {
			this.parent = null;
		}
		this.sink = sink;
	}
	@Override
	public void visit(Visitor<D> visitor, char[] key, int index, boolean exact) {
		// if you got here at the end of a chain and the
		// match is not exact that's ok, this is optional!
		// so visit!
		if(index >= key.length) {
			if(!exact && !visitor.construct()) {
				visitor.at(this, index, key, exact);
			}
			return;
		}
		
		boolean end = false;
		
		Character local = Character.valueOf(key[index]);
		if(this.matcher.match(local, exact)) {
			if(index == key.length - 1) {
				visitor.at(this, index, key, exact);
				end = true;
			}
			if(!exact && this.next != null) {
				this.next.visit(visitor, key, index, exact);
			}
			index++;
		}
		
		if(!end && exact && visitor.construct() && this.next == null) {
			Character next = key[index];
			if(this.next == null) {
				this.next = this.construct(next);
			}
		}
		
		if(this.next != null) {
			this.next.visit(visitor, key, index, exact);
		}
	}

	@Override
	public boolean attracts(boolean exact) {
		return !exact;
	}

	@Override
	public void print(String prefix, String describe, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + " " + describe + " " + this.matcher.value() + " -> " + this.contentString());
        if(this.next != null) {
        	this.next.print(prefix + (isTail ? "    " : "│   ") , "[NEXT]", true);
        }
	}
	@Override
	public boolean optional() {
		return true;
	}

}
