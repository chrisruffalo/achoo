package com.em.achoo.configure;

public enum ConfigurationKeys {

	// attributes
	ACHOO_ACTOR_SYSTEM("com.em.achoo.AchooActorSystem.key"),
	ACHOO_CONFIG("com.em.achoo.Configuration.key")
	
	;
	
	
	private String key = null;
	
	private ConfigurationKeys(String key) {
		this.key = key;
	}
	
	public String toString() {
		return this.key;
	}
	
}
