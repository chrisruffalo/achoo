package com.em.achoo;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.typesafe.config.Config;

@RunWith(Arquillian.class)
public abstract class AchooBaseTest {

	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive archive = ShrinkWrap.create(JavaArchive.class);
		
		//add package
		archive.addPackages(true, "com.em.achoo");
		
		//add achoo configuration / logging configuraiton resources
		archive.addAsResource("logback-test.xml");
		archive.addAsResource("achoo.conf");
		archive.addAsResource("achoo-default.conf");
		
		//add manifest resources
		archive.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		archive.addAsManifestResource(EmptyAsset.INSTANCE, "persistence.xml");
		
		return archive;
	}
	
	//@Inject
	//private Logger logger;
	
	@Inject
	private Achoo achoo;
	
	@Inject
	private Event<Config> configEvent;
	
	@Before
	public void startAchooAndBroadcastConfigurationEvent() {
		//start achoo
		this.achoo.start();
		
		//create configuration broadcast event		
		this.configEvent.fire(this.achoo.getConfig());
	}
	
	@After
	public void killSystem() {
		//stop achoo
		this.getAchoo().stop();
		
		//close achoo
		this.getAchoo().close();
	}

	
	public Achoo getAchoo() {
		return this.achoo;
	}
	
}
