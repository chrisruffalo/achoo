package com.achoo.topicstore.tst;

import com.achoo.topicstore.tst.config.SearchConfigurationImpl;

public class SearchTreeBuilder {

	private SearchConfigurationImpl configuration;
	
	public SearchTreeBuilder() {
		 this.configuration = new SearchConfigurationImpl();
	}
	
	public SearchTreeBuilder addWildcardCharacter(Character wildcard) {
		this.configuration.addWildcard(wildcard);
		return this;
	}
	
	public SearchTreeBuilder addAnyMatchCharacter(Character anyMatch) {
		this.configuration.addAny(anyMatch);
		return this;
	}
	
	public SearchTreeBuilder addOptionalCharacter(Character optional) {
		this.configuration.addOptional(optional);
		return this;
	}
	
	public SearchTreeBuilder caseSensitive(boolean sensitive) {
		this.configuration.setCaseSensitive(sensitive);
		return this;
	}
	
	public <D> SearchTree<D> build() {
		return new SearchTree<>(this.configuration.copy());
	}
}
