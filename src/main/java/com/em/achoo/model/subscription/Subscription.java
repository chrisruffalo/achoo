package com.em.achoo.model.subscription;

import com.em.achoo.model.interfaces.IExchange;
import com.em.achoo.model.interfaces.ISubscription;

public abstract class Subscription implements ISubscription {

	private String id = null;
	
	private IExchange exchange = null;
	
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public IExchange getExchange() {
		return this.exchange;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setExchange(IExchange exchange) {
		this.exchange = exchange;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Subscription))
			return false;
		Subscription other = (Subscription) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
}
