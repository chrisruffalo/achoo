package com.em.achoo;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.util.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.SmallestMailboxRouter;

import com.beust.jcommander.JCommander;
import com.em.achoo.actors.AchooActorSystem;
import com.em.achoo.actors.exchange.ExchangeManager;
import com.em.achoo.actors.sender.SenderActor;
import com.em.achoo.configure.AchooCommandLine;
import com.em.achoo.configure.ConfigurationUtility;
import com.em.achoo.configure.IServerConfiguration;
import com.em.achoo.endpoint.FavoriteIcon;
import com.em.achoo.endpoint.broker.MessageRecieveEndpoint;
import com.em.achoo.endpoint.broker.SubscriptionManagerEndpoint;
import com.em.achoo.endpoint.management.ManagementEndpoint;
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
	
	private AchooCommandLine commandLine = null;
	
	private Config configuration = null;
	
	public Achoo(AchooCommandLine commandLine) {
		this.commandLine = commandLine;
		
		//get configuration file from command line options
		File configFile = this.commandLine.getConfigurationFile();

		//parse found file
		this.configuration = ConfigurationUtility.getConfiguration(configFile, "achoo");		
		
		//instantiate achoo system
		boolean clustered = configuration.getBoolean("achoo.clustering");
		String systemName = configuration.getString("achoo.node-name");
		if(systemName == null || systemName.isEmpty()) {
			systemName = AchooActorSystem.ACHOO_DEFAULT_ACTOR_SYSTEM_NAME;
		}
		
		//get system
		this.instanceActorSystem = new AchooActorSystem(systemName, this.configuration, clustered);
	}
	
	public void await() {
		try {
			this.latch.await();
		} catch (InterruptedException e) {
			this.log.error("Could not await shutdown: {}", e.getMessage());
		}
	}
	
	public AchooActorSystem getAchooActorSystem() {
		return this.instanceActorSystem;
	}
	
	public ActorRef getExchangeManagerRef() {
		return this.getAchooActorSystem().getSystem().actorFor("/user/" + ExchangeManager.ACHOO_EXCHANGE_MANAGER_NAME);
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
	
	public void start() {
		if(this.started) {
			return;
		}
		
		//get versions 
		String akkaVersion = this.configuration.getString("akka.version");
		String achooVersion = this.configuration.getString("achoo.version");
		
		this.log.info("Starting Achoo version {} (based on Akka {})", achooVersion, akkaVersion);
		
		//get sizes for pools from configuration
		int exchangeManagerRouterSize = this.configuration.getInt("achoo.exchange-managers");
		int senderRouterSize = this.configuration.getInt("achoo.sender-pool");		
		
		//create exchange manager (pool)		
		Props exchangeManagerProps = new Props(ExchangeManager.class);
		if(exchangeManagerRouterSize > 1) {
			exchangeManagerProps = exchangeManagerProps.withRouter(new SmallestMailboxRouter(exchangeManagerRouterSize));
		}
		ActorRef newManager = this.instanceActorSystem.getSystem().actorOf(exchangeManagerProps, ExchangeManager.ACHOO_EXCHANGE_MANAGER_NAME);
		this.log.info("Created exchange manager at {}", newManager.path().toString());
		
		//create sender pool
		Props senderPoolProps = new Props(SenderActor.class);
		if(senderRouterSize > 1) {
			senderPoolProps = senderPoolProps.withRouter(new SmallestMailboxRouter(senderRouterSize));
		}
		ActorRef senderPool = this.instanceActorSystem.getSystem().actorOf(senderPoolProps, "senders");
		this.log.info("Created sender pool at {}", senderPool.path().toString());
	}
	
	public void startEndpoints() {		
		//create external (servlet/rest) endpoints		
		Class<?>[] endpoints = new Class<?>[]{
				FavoriteIcon.class,
				ManagementEndpoint.class,
				MessageRecieveEndpoint.class,
				SubscriptionManagerEndpoint.class,
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
			serverConfiguration.setAchooReference(this);
			
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
	
	public void close() {
		//get system to shut it down
		ActorSystem achooSystem = this.getAchooActorSystem().getSystem();
		
		//finally, shut down actor system
		achooSystem.shutdown();
		
		this.log.info("Shutdown akka system... ");
		
		try {
			achooSystem.awaitTermination(Duration.parse("5 seconds"));
		} catch (Exception ex) {
			this.log.warn("Achoo's akka system was killed because it took longer than 5 seconds to shut down");
		}
		
		//stop
		this.log.info("Achoo's akka-subsystem shutdown.");
	}
	
	public static void main(String[] args) {
		
		//Logger logger = LoggerFactory.getLogger(Achoo.class.getName() + "-main");
		
		//use jcommander to parse arguments
		AchooCommandLine commandValues = new AchooCommandLine();
		new JCommander(commandValues, args);
		
		if(commandValues.isHelp()) {
			//print help
			System.out.println("HALP!");
			//quit
			return;
		}
		
		//create achoo object from command line values
		final Achoo achoo = new Achoo(commandValues); 

		//start achoo
		achoo.start();
		
		///start endpoints
		achoo.startEndpoints();
		
		//wait for kill message to come, sometime
		achoo.await();
		
		/*==================================================================
		 * The Akka subsystem MUST BE shutdown outside of the achoo system 
		 * so it is shutdown when control is returned to the main thread 
		 * here so that Akka can stop cleanly  
		 ==================================================================*/
		achoo.close();
	}

	
}
