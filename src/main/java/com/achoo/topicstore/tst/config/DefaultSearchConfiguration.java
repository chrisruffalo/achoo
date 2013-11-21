package com.achoo.topicstore.tst.config;

public class DefaultSearchConfiguration extends SearchConfigurationImpl {

	public DefaultSearchConfiguration() {
		super();
		
		this.addAny('#');
		this.addOptional('?');
		this.addWildcard('*');
		
		this.setCaseSensitive(true);
	}
	
}
