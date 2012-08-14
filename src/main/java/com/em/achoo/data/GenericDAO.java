package com.em.achoo.data;

import java.io.Serializable;
import java.util.List;

public interface GenericDAO<T, PK extends Serializable> {

	public T persist (T t);
	
	public T read(PK id);
	
	public T update(T t);
	
	public void delete(T t);
	
	public List<T> list();
		
}
