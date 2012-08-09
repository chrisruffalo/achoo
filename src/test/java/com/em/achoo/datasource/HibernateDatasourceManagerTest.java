package com.em.achoo.datasource;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Assert;
import org.junit.Test;

import com.em.achoo.configure.ConfigurationUtility;
import com.typesafe.config.Config;

public class HibernateDatasourceManagerTest {

	@Test
	public void testFactoryCreate() {
		
		//load config
		Config testHibernateConfig = ConfigurationUtility.getConfiguration("achoo-datasource-hsqldb");
		
		HibernateDatasourceManager.init(testHibernateConfig);
		
		EntityManagerFactory factory = HibernateDatasourceManager.get();
		
		EntityManager em = factory.createEntityManager();
		
		Assert.assertTrue(em.isOpen());
		
	}
	
}
