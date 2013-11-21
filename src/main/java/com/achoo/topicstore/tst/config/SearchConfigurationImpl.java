package com.achoo.topicstore.tst.config;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class SearchConfigurationImpl implements SearchConfiguration {
	
	private Set<Character> wildcards;
	
	private Set<Character> optionals;
	
	private Set<Character> any;
	
	boolean isCaseSensitive = true;
	
	public SearchConfigurationImpl() {
		this.wildcards = new TreeSet<>();
		this.optionals = new TreeSet<>();
		this.any = new TreeSet<>();
	}
	
	public void addWildcard(Character wildcard) {
		if(wildcard != null) {
			this.wildcards.add(Character.valueOf(wildcard));	
		}
	}
	
	@Override
	public Set<Character> wildcards() {
		return Collections.unmodifiableSet(this.wildcards);
	}
	
	public void addOptional(Character optional) {
		if(optional != null) {
			this.optionals.add(Character.valueOf(optional));	
		}
	}

	@Override
	public Set<Character> optional() {
		return Collections.unmodifiableSet(this.optionals);
	}

	public void addAny(Character any) {
		if(any != null) {
			this.any.add(Character.valueOf(any));	
		}
	}
	
	@Override
	public Set<Character> any() {
		return Collections.unmodifiableSet(this.any);
	}
	
	public void setCaseSensitive(boolean sensitive) {
		this.isCaseSensitive = sensitive;
	}

	@Override
	public boolean caseSensitive() {
		return this.isCaseSensitive;
	}

	public SearchConfiguration copy() {
		SearchConfigurationImpl copy = new SearchConfigurationImpl();
		copy.wildcards = new TreeSet<>(this.wildcards);
		copy.optionals = new TreeSet<>(this.optionals);
		copy.any = new TreeSet<>(this.any);
		copy.isCaseSensitive = this.isCaseSensitive;
		
		return copy;
	}
}
