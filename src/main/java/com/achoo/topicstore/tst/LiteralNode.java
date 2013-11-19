package com.achoo.topicstore.tst;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

class LiteralNode<D> extends AbstractNode<D> {
	
	private Character point;
	
	private InternalNode<D> higher;
	
	private InternalNode<D> lower;
	
	private Map<Integer, Set<D>> depthMap;
	
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
		if(this.point.equals(local)) {
			if(index == key.length - 1) {
				if(this.depthMap != null) {
					Set<D> contents = this.depthMap.get(index);
					if(contents != null) {
						results.addAll(contents);
					}
				}
			} else {
				this.lookup(results, key, index+1, exact);
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
		if(this.point.equals(local)) {
			if(index == key.length - 1) {
				if(values != null && !values.isEmpty()) {
					if(this.depthMap == null) {
						this.depthMap = new TreeMap<>();
					}
					Set<D> contents = this.depthMap.get(index);
					if(contents == null) {
						contents = new TreeSet<>();
						this.depthMap.put(index, contents);
					}
					for(D value : values) {
						if(value != null) {
							contents.add(value);
						}
					}
				}
			} else {
				this.add(key, index+1, values);
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
        	this.higher.print(prefix + (isTail ? "    " : "│   ") , "[HIGH]", this.lower == null);
        }
        if(this.lower != null) {
        	this.lower.print(prefix + (isTail ? "    " : "│   ") , "[LOW]", true);
        }
    }
	
	@Override
	public boolean extend(boolean exact) {
		return false;
	}
	
	private String contentString() {
		if(this.depthMap == null || this.depthMap.isEmpty()) {
			return "{}";
		}
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for(Map.Entry<Integer, Set<D>> entry : this.depthMap.entrySet()) {
			Integer key = entry.getKey();
			Set<D> set = entry.getValue();
			if(set == null || set.isEmpty()) {
				continue;
			}
			if(!first) {
				builder.append(", ");
			}
			first = false;
			builder.append(key);
			builder.append(":{");
			boolean innerFirst = true;
			for(D value : set) {
				if(value == null) {
					continue;
				}
				String printString = String.valueOf(value);
				if(printString == null || printString.isEmpty()) {
					continue;
				}
				if(!innerFirst) {
					builder.append(", ");
				}
				innerFirst = false;
				builder.append(printString);
			}
			builder.append("}");
		}
		
		return builder.toString();
	}
	
	Character point() {
		return this.point;
	}
	
	InternalNode<D> low() {
		return this.lower;
	}
	
	InternalNode<D> high() {
		return this.higher;
	}

}
