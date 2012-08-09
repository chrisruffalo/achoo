package com.em.achoo.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.ejb.HibernatePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;

public class HibernateDatasourceManager {

	private static final Logger logger = LoggerFactory.getLogger(HibernateDatasourceManager.class);
	
	private static final String ACHOO_DATA_SOURCE_NAME = "achoo-data-source"; 
	
	public static void init(Config config) {
		
		Config dataSourceConfig = config.getConfig("achoo.data-source");
		
		//set defaults
		String name = HibernateDatasourceManager.ACHOO_DATA_SOURCE_NAME;
		String driver = "org.hsqldb.jdbcDriver";
		String url = "jdbc:hsqldb:mem:achoo";
		String dialect = "org.hibernate.dialect.HSQLDialect";
		String user = "sa";
		String password = "";
		
		//load configuration
		if(dataSourceConfig != null) {
			if(!"hibernate".equalsIgnoreCase(dataSourceConfig.getString("type"))) {
				HibernateDatasourceManager.logger.warn("Tried to initalize a data source named '{}', but it was not of type 'hibernate'.");
				return;
			}
			
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
		
		//create persistence unit
		EntityManagerFactory factory = Persistence.createEntityManagerFactory(name, configuration);

		//save factory to jndi
		try {
			Context context = new InitialContext();
			context.bind("java:achoo/persistence/" + name + "/entitymanagerfactory", factory);
		} catch (NamingException e) {
			throw new IllegalStateException("Could not bind initial provider.", e);
		}	
		
	}
	
	public static EntityManagerFactory get() {
		try {
			Context context = new InitialContext();
			Object lookupObject = context.lookup("java:achoo/persistence/" + HibernateDatasourceManager.ACHOO_DATA_SOURCE_NAME + "/entitymanagerfactory");
			if(lookupObject instanceof EntityManagerFactory) {
				return (EntityManagerFactory)lookupObject;
			} else {
				throw new IllegalStateException("Stored object is not an EntityManagerFactory or is null");
			}
		} catch (NamingException e) {
			throw new IllegalStateException("Cannot proceed without a valid Entity Manager", e);
		}
	}
	
}
