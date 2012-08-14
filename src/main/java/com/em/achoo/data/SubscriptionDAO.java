package com.em.achoo.data;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class SubscriptionDAO extends AbstractDAO<SubscriptionDO, String> {

	public List<SubscriptionDO> listSubscriptionsForExchange(String exchange) {
		//create criteria query
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<SubscriptionDO> query = builder.createQuery(SubscriptionDO.class);
		
		//get query root
		Root<SubscriptionDO> container = query.from(SubscriptionDO.class);
		
		//create/set query options
		query.select(container).where(builder.equal(container.get("exchangeName"), exchange));		
		
		//return query results
		return this.entityManager.createQuery(query).getResultList();
	}
	
}
