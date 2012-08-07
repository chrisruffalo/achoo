package com.em.achoo;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;

import com.beust.jcommander.JCommander;
import com.em.achoo.actors.AchooActorSystem;
import com.em.achoo.actors.AchooManager;
import com.em.achoo.actors.factory.AchooManagerFactory;
import com.em.achoo.configure.AchooCommandLine;
import com.em.achoo.configure.ConfigurationUtility;
import com.em.achoo.configure.IServerConfiguration;
import com.em.achoo.endpoint.FavoriteIcon;
import com.em.achoo.endpoint.broker.MessageRecieveEndpoint;
import com.em.achoo.endpoint.broker.SubscriptionManagerEndpoint;
import com.em.achoo.endpoint.management.ManagementEndpoint;
import com.em.achoo.endpoint.test.Echo;
import com.em.achoo.model.management.StartMessage;
import com.em.achoo.server.IServer;
import com.em.achoo.server.ServerFactory;
import com.em.achoo.server.ServerType;
import com.em.achoo.server.impl.BasicServerConfiguration;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

public class Achoo {

	private CountDownLatch latch = new CountDownLatch(1);
	
	private Logger log = LoggerFactory.getLogger(Achoo.class);

	private boolean started = false;
	
	private Set<IServer> servers = null;
	
	private AchooActorSystem instanceActorSystem = null;
	
	//private AchooCommandLine commandLine = null;
	
	private Config configuration = null;
	
	public Achoo(AchooActorSystem actorSystem, AchooCommandLine commandLine, Config configuration) {
		this.instanceActorSystem = actorSystem;
		//this.commandLine = commandLine;
		this.configuration = configuration;
	}
	
	public void await() {
		try {
			this.latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		//stop servers
		if(this.servers != null) {
			for(IServer server : this.servers) {
				this.log.info("Stopping server class: {}", server.getClass().getName());
				
				server.stop();
			}
		}
		
		//count down the latch, and release the main server
		this.latch.countDown();
	}
	
	public void start(StartMessage message) {
		if(this.started) {
			return;
		}

		Class<?>[] endpoints = new Class<?>[]{
				FavoriteIcon.class,
				ManagementEndpoint.class,
				MessageRecieveEndpoint.class,
				SubscriptionManagerEndpoint.class,
				Echo.class
		};		
		
		Set<IServerConfiguration> configurationSet = new HashSet<IServerConfiguration>();
		
		List<? extends Config> list = Collections.emptyList();
		try {
			list = this.configuration.getConfigList("achoo.servers");
		} catch (ConfigException.Missing e) {
			this.log.warn("No configuration found at key '{}', no HTTP endpoints will be started", "achoo.servers");
		}
		
		for(Config serverObject : list) {

			String bindAddress = serverObject.getString("bind");
			int port = serverObject.getInt("port");
			String typeString = serverObject.getString("type");
			ServerType type = ServerType.getServerForString(typeString);
						
			BasicServerConfiguration serverConfiguration = new BasicServerConfiguration(bindAddress, port, type, endpoints);
			serverConfiguration.setRawConfiguration(this.configuration);
			serverConfiguration.setAchooActorSystem(this.instanceActorSystem);
			
			configurationSet.add(serverConfiguration);
		}
		
		this.servers = new HashSet<IServer>(configurationSet.size());
		for(IServerConfiguration serverConfig : configurationSet) {
			
			IServer server = ServerFactory.getServer(serverConfig.getServerType());
			
			if(server != null) {
				this.log.info("Starting {} at bind {} on port {}", new Object[]{serverConfig.getServerType(), serverConfig.getBindAddress(), serverConfig.getPort()});
				server.start(serverConfig);
				
				//add to start
				this.servers.add(server);
			} else {
				this.log.info("No implementation found for server type '{}'", serverConfig.getServerType());
			}
		}
		
		this.started = true;
	}
	
	public static void main(String[] args) {
		
		Logger logger = LoggerFactory.getLogger(Achoo.class.getName() + "-main");
		
		//use jcommander to parse arguments
		AchooCommandLine commandValues = new AchooCommandLine();
		new JCommander(commandValues, args);
		
		if(commandValues.isHelp()) {
			//print help
			System.out.println("HALP!");
			//quit
			return;
		}		

		//get configuration file from command line options
		File configFile = commandValues.getConfigurationFile();

		//parse found file
		Config achooConfig = ConfigurationUtility.getConfiguration(configFile, "achoo");		
		
		//instantiate achoo system
		boolean clustered = achooConfig.getBoolean("achoo.clustering");
		String systemName = achooConfig.getString("achoo.node-name");
		if(systemName == null || systemName.isEmpty() || !clustered) {
			systemName = AchooActorSystem.ACHOO_DEFAULT_ACTOR_SYSTEM_NAME;
		}
		
		//get system
		AchooActorSystem achooSystem = new AchooActorSystem(systemName, achooConfig, clustered);
		ActorSystem system = achooSystem.getSystem();
		
		//create achoo object
		final Achoo achoo = new Achoo(achooSystem, commandValues, achooConfig); 
		
		//create manager factory and manage item
		AchooManagerFactory factory = new AchooManagerFactory(achoo);
		ActorRef ref = system.actorOf(new Props(factory), AchooManager.NAME);
		
		//start with akka system
		StartMessage start = new StartMessage();
		
		//dispatch start message
		ref.tell(start);

		//wait for kill message to come, sometime
		achoo.await();
		
		//shutdown user created actors
		ActorSelection selection = system.actorSelection("/user/*");
		selection.tell(PoisonPill.getInstance());
		
		//finally, shut down actor system
		system.shutdown();
		system.dispatcher().shutdown();
		system.awaitTermination();
		
		logger.info("Achoo akka-subsystem shutdown.");
	}

	
}
