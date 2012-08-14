package com.em.achoo.data;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.ejb.HibernatePersistence;
import org.slf4j.Logger;

import com.em.achoo.weld.AchooBootstrap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@Singleton
public class HibernateDatasourceManager {

	private static final String ACHOO_DATA_SOURCE_NAME = "achoo-data-source";
	
	@Inject
	private Logger logger;
	
	private EntityManagerFactory factory = null;

	public void init(@Observes Config config) {
		
		Config dataSourceConfig = ConfigFactory.empty(); 
		if(config.hasPath("achoo.data-source")) {
			dataSourceConfig = config.getConfig("achoo.data-source");
		}
		
		//default data source name
		String name = HibernateDatasourceManager.ACHOO_DATA_SOURCE_NAME;
		
		if(!dataSourceConfig.hasPath("type")) {
			this.logger.warn("Tried to initalize a data source named '{}' but it does not exist in configuration files", name);
			return;
		}
			
		if(!"hibernate".equalsIgnoreCase(dataSourceConfig.getString("type"))) {
			this.logger.warn("Tried to initalize a data source named '{}', but it was not of type 'hibernate'", name);
			return;
		}

		//set defaults
		String driver = "org.hsqldb.jdbcDriver";
		String url = "jdbc:hsqldb:mem:achoo";
		String dialect = "org.hibernate.dialect.HSQLDialect";
		String user = "sa";
		String password = "";

		//load configuration
		if(dataSourceConfig.hasPath("driver")) {
			driver = dataSourceConfig.getString("driver");
		}

		if(dataSourceConfig.hasPath("url")) {
			url = dataSourceConfig.getString("url");
		}

		if(dataSourceConfig.hasPath("dialect")) {
			dialect = dataSourceConfig.getString("dialect");
		}

		if(dataSourceConfig.hasPath("user")) {
			user = dataSourceConfig.getString("user");
		}

		if(dataSourceConfig.hasPath("password")) {
			password = dataSourceConfig.getString("password");
		}

		//create hibernate flavor data source
		Map<String, Object> configuration = new HashMap<String, Object>();

		//configure persistence unit
		configuration.put("javax.persistence.provider", HibernatePersistence.class.getName());
		configuration.put("javax.persistence.transactionType", "RESOURCE_LOCAL");
		configuration.put("javax.persistence.jdbc.driver", driver);
		configuration.put("javax.persistence.jdbc.url", url);
		configuration.put("hibernate.dialect", dialect);
		configuration.put("javax.persistence.jdbc.user", user);
		configuration.put("javax.persistence.jdbc.password", password);
		configuration.put("hibernate.hbm2ddl.auto", "update");

		//create persistence factory
		this.factory = Persistence.createEntityManagerFactory(name, configuration);

	}

	@Produces
	@AchooBootstrap
	public EntityManagerFactory getEntityManagerFactory() {
		return this.factory;
	}
	
	@Produces
	@AchooBootstrap
	public EntityManager getEntityManager() {
		return this.getEntityManagerFactory().createEntityManager();
	}

}
