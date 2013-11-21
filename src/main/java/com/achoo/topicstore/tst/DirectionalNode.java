package com.achoo.topicstore.tst;

import java.util.Collection;
import java.util.Set;

import com.achoo.topicstore.tst.matcher.Matcher;

class DirectionalNode<D> extends DepthContentNode<D> {
	
	private Matcher matcher;
	
	private InternalNode<D> higher;

	private InternalNode<D> same;
	
	private InternalNode<D> lower;
	
	public DirectionalNode(Matcher matcher) {
		this.matcher = matcher;
	}
	
	public void lookup(Set<D> results, char[] key, int index, boolean exact) {
		// nothing to do here
		if(index >= key.length) {
			return;
		}
		
		Character local = Character.valueOf(key[index]);
		if(this.matcher.match(local, exact)) {
			if(index == key.length - 1) {
				results.addAll(this.getContentAtDepth(key.length));
			} else {
				if(this.same != null) {
					this.same.lookup(results, key, index+1, exact);
				}
			}
		}
		
		if(this.higher != null && (this.matcher.compare(local) < 0 || this.higher.attracts(exact))) {
			this.higher.lookup(results, key, index, exact);
		} 
		
		if(this.lower != null  && (this.matcher.compare(local) > 0 || this.lower.attracts(exact))) {
			this.lower.lookup(results, key, index, exact);
		}
	}

	@Override
	public void add(char[] key, int index, Collection<D> values) {
		// nothing to do here
		if(index >= key.length) {
			return;
		}
		
		Character local = Character.valueOf(key[index]);
		if(this.matcher.match(local, true)) {
			if(index == key.length - 1) {
				this.addContentAtDepth(key.length, values);
			} else {
				Character next = key[index+1];
				if(this.same == null) {
					this.same = NodeFactory.create(next);
				}				
				this.same.add(key, index+1, values);
			}
		} else if(this.matcher.compare(local) < 0) {
			if(this.higher == null) {
				this.higher = NodeFactory.create(local);
			}
			this.higher.add(key, index, values);
		} else if(this.matcher.compare(local) > 0) {
			if(this.lower == null) {
				this.lower = NodeFactory.create(local);
			} 
			this.lower.add(key, index, values);
		}
	}
	
	@Override
	public void print(String prefix, String describe,  boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + " " + describe + " " + this.matcher.value() + " -> " + this.contentString());
        if(this.higher != null) {
        	this.higher.print(prefix + (isTail ? "    " : "│   ") , "[HIGH]", this.lower == null && this.same == null);
        }
        
        if(this.same != null) {
        	this.same.print(prefix + (isTail ? "    " : "│   ") , "[SAME]", this.lower == null);
        }

        if(this.lower != null) {
        	this.lower.print(prefix + (isTail ? "    " : "│   ") , "[LOW]", true);
        }
    }
	
	@Override
	public boolean attracts(boolean exact) {
		return this.matcher.attracts(exact);
	}
	
	Character value() {
		return this.matcher.value();
	}
	
	InternalNode<D> low() {
		return this.lower;
	}
	
	InternalNode<D> same() {
		return this.same;
	}
	
	InternalNode<D> high() {
		return this.higher;
	}

}
