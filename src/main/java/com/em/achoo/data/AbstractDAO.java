package com.em.achoo.data;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import com.em.achoo.weld.AchooBootstrap;


public class AbstractDAO<T, PK extends Serializable> implements GenericDAO<T, PK> {

	protected Class<T> entityClass;

	@Inject
	@AchooBootstrap
    protected EntityManager entityManager;

    @SuppressWarnings("unchecked")
	public AbstractDAO() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<T>)genericSuperclass.getActualTypeArguments()[0];
    }

    @Override
    public T persist(T t) {
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
    
    @Override
	public List<T> list() {
		//create
    	CriteriaBuilder builder = this.entityManager.getEntityManagerFactory().getCriteriaBuilder();
    	
    	//create query
    	CriteriaQuery<T> query = builder.createQuery(this.entityClass);
    	
    	return this.entityManager.createQuery(query).getResultList();
    }
   

}
