package com.achoo.model;

public abstract class Item {

	private String uuid;
	
	public void setUuid(String tokenUuid) {
		this.uuid = tokenUuid;		
	}
	
	public String getUuid() {
		return this.uuid;
	}
	
}
