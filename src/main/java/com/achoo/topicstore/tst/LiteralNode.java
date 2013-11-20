package com.achoo.topicstore.tst;

import java.util.Collection;
import java.util.Set;

class LiteralNode<D> extends DepthContentNode<D> {
	
	private Character point;
	
	private InternalNode<D> higher;
	
	private InternalNode<D> lower;
	
	private InternalNode<D> same;
	
	public LiteralNode(char local) {
		// set value
		this.point = Character.valueOf(local);
	}
	
	public void lookup(Set<D> results, char[] key, int index, boolean exact) {
		// nothing to do here
		if(index >= key.length) {
			return;
		}
		
		Character local = Character.valueOf(key[index]);
		if(this.matches(local, exact)) {
			if(index == key.length - 1) {
				results.addAll(this.getContentAtDepth(key.length));
			} else {
				if(this.same != null) {
					this.same.lookup(results, key, index+1, exact);
				}
			}
		}
		
		if(this.higher != null && (this.point.compareTo(local) < 0 || this.higher.extend(exact))) {
			this.higher.lookup(results, key, index, exact);
		} 
		
		if(this.lower != null  && (this.point.compareTo(local) > 0 || this.lower.extend(exact))) {
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
		if(this.matches(local, true)) {
			if(index == key.length - 1) {
				this.addContentAtDepth(key.length, values);
			} else {
				Character next = key[index+1];
				if(this.same == null) {
					this.same = NodeFactory.create(next);
				}				
				this.same.add(key, index+1, values);
			}
		} else if(this.point.compareTo(local) < 0) {
			if(this.higher == null) {
				this.higher = NodeFactory.create(local);
			}
			this.higher.add(key, index, values);
		} else if(this.point.compareTo(local) > 0) {
			if(this.lower == null) {
				this.lower = NodeFactory.create(local);
			} 
			this.lower.add(key, index, values);
		}
	}
	
	@Override
	public void print(String prefix, String describe,  boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + " " + describe + " " + this.point + " -> " + this.contentString());
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
	public boolean extend(boolean exact) {
		return false;
	}
	
	@Override
	public boolean matches(char value, boolean exact) {
		return this.point.equals(Character.valueOf(value));	
	}
	
	Character point() {
		return this.point;
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
