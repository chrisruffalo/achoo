package com.em.achoo.model.exchange;


public class ExchangeInformation {

	private String name = null;
	
	private ExchangeType type = ExchangeType.TOPIC;
	
	public void setName(String name) {
		this.name = name;
	}

	public void setType(ExchangeType type) {
		this.type = type;
	}
	
	public String getName() {
		return this.name;
	}

	public ExchangeType getType() {
		return this.type;
	}
	
	public String toString() {
		return String.format("Exchange={Name=%s, Type=%s}", this.getName(), this.getType());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExchangeInformation other = (ExchangeInformation) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}	
	
}
