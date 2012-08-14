package com.em.achoo.data;

import java.io.Serializable;

public interface GenericDAO<T, PK extends Serializable> {

	public T create (T t);
	
	public T read(PK id);
	
	public T update(T t);
	
	public void delete(T t);
	
		
}
