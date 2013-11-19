package com.achoo.topicstore.tst;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class AnyCharacterNode<D> extends AbstractNode<D> {
	
	public static final Character ANY_CHARACTER = '#';
	
	private InternalNode<D> match;
	
	private InternalNode<D> shunt;

	private Set<D> contents;
	
	@Override
	public void lookup(Set<D> results, char[] key, int index, boolean exact) {
		// nothing to do here
		if(index >= key.length) {
			return;
		}
		
		Character local = Character.valueOf(key[index]);
		
		// exact match
		if(exact && AnyCharacterNode.ANY_CHARACTER.equals(local)) {
			if(index == key.length - 1) {
				if(this.contents != null && !this.contents.isEmpty()) {
					results.addAll(this.contents);
				}
			} else if(this.match != null) {
				this.match.lookup(results, key, index+1, exact);
			}
		} else if(exact) {
			if(this.shunt != null) {
				this.shunt.lookup(results, key, index, exact);
			}
		} else {
			if(index == key.length - 1) {
				if(this.contents != null && !this.contents.isEmpty()) {
					results.addAll(this.contents);
				}
			} else if(this.match != null) {
				this.match.lookup(results, key, index+1, exact);
			}
			
			if(this.shunt != null) {
				this.shunt.lookup(results, key, index, exact);
			}
		}
	}

	@Override
	public void add(char[] key, int index, Collection<D> values) {
		// nothing to do here
		if(index >= key.length) {
			return;
		}
		
		Character local = Character.valueOf(key[index]);
		if(AnyCharacterNode.ANY_CHARACTER.equals(local)) {
			// matches!  use match branch!
			if(index == key.length - 1) {
				if(values != null && !values.isEmpty()) {
					if(this.contents == null) {
						this.contents = new TreeSet<>();
					}
					for(D value : values) {
						if(value != null) {
							this.contents.add(value);
						}
					}
				}				
			} else {
				if(this.match == null) {
					Character nextCharacter = Character.valueOf(key[index+1]);
					this.match = NodeFactory.create(nextCharacter);
				}
				this.match.add(key, index+1, values);
			}
		} else {
			// does not match! *shunt* away
			if(this.shunt == null) {
				this.shunt = NodeFactory.create(local);
			}
			this.shunt.add(key, index, values);
		}
	}


	@Override
	public boolean extend(boolean exact) {
		return !exact;
	}
	
	@Override
    public void print(String prefix, String describe,  boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + " " + describe + " " + AnyCharacterNode.ANY_CHARACTER);
        if(this.match != null) {
        	this.match.print(prefix + (isTail ? "    " : "│   "), "[MATCH]", this.shunt == null);
        }
        if(this.shunt != null) {
        	this.shunt.print(prefix + (isTail ? "    " : "│   "), "[SHUNT]", true);
        }
    }
	
	InternalNode<D> match() {
		return this.match;				
	}
	
	InternalNode<D> shunt() {
		return this.shunt;
	}


}
