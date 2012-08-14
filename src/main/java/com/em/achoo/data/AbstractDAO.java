package com.em.achoo.data;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


public class AbstractDAO<T, PK extends Serializable> implements GenericDAO<T, PK> {

	protected Class<T> entityClass;

    @PersistenceContext
    protected EntityManager entityManager;

    @SuppressWarnings("unchecked")
	public AbstractDAO() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<T>)genericSuperclass.getActualTypeArguments()[0];
    }

    @Override
    public T create(T t) {
        this.entityManager.persist(t);
        return t;
    }

    @Override
    public T read(PK id) {
        return this.entityManager.find(entityClass, id);
    }

    @Override
    public T update(T t) {
        return this.entityManager.merge(t);
    }

    @Override
    public void delete(T t) {
        t = this.entityManager.merge(t);
        this.entityManager.remove(t);
    }
    
   

}
