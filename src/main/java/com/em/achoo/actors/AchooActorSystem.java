package com.em.achoo.actors;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.cluster.Cluster;
import akka.japi.Creator;

import com.typesafe.config.Config;

public class AchooActorSystem {

	public static final String ACHOO_DEFAULT_ACTOR_SYSTEM_NAME = "achoo";
	
	//private Logger log = LoggerFactory.getLogger(AchooActorSystem.class);
	
	private ActorSystem system = null;
	
	private String name = null;
	
	private boolean clustered = false;

	public AchooActorSystem() {
		this(AchooActorSystem.ACHOO_DEFAULT_ACTOR_SYSTEM_NAME, null, false);
	}
	
	public AchooActorSystem(boolean clustered) {
		this(AchooActorSystem.ACHOO_DEFAULT_ACTOR_SYSTEM_NAME, null, clustered);
	}
	
	public AchooActorSystem(String name, Config configuration, boolean clustered) {
		//save name
		this.name = name;
		
		//save cluster status 
		this.clustered = clustered;
		
		//if a configuration is not provided: just create named actor system and use classpath/akka reference configurations
		if(configuration == null) {
			this.system = ActorSystem.create(this.name);
		} else {
			//use loaded configuration to bootstrap it
			this.system = ActorSystem.create(this.name, configuration);
		}
		
		//enable clustering
		if(this.clustered) {
			Cluster.apply(this.system);
		}		
	}
	
	public ActorSystem getSystem() {
		return this.system;
	}
	
	public static <T> T getTypedActor(ActorSystem system, Class<T> typedActorClass) {
		return AchooActorSystem.getTypedActor(system, new TypedProps<T>(typedActorClass));
	}
	
	public static <T,Y extends T> T getTypedActor(ActorSystem system, Class<T> typedActorClass, Class<Y> implementingClass) {
		return AchooActorSystem.getTypedActor(system, new TypedProps<Y>(typedActorClass, implementingClass));
	}
	
	public static <T,Y extends T> T getTypedActor(ActorSystem system, Class<T> typedActorClass, Class<Y> implementingClass, String name) {
		return AchooActorSystem.getTypedActor(system, new TypedProps<Y>(typedActorClass, implementingClass), name);
	}
	
	public static <T, Y extends T> Y getTypedActor(ActorSystem system, Class<T> typedActorClass, Creator<Y> creator) {
		return AchooActorSystem.getTypedActor(system, new TypedProps<Y>(typedActorClass, creator));
	}
	
	public static <T> T getTypedActor(ActorSystem system, Class<T> typedActorClass, String name) {
		return AchooActorSystem.getTypedActor(system, new TypedProps<T>(typedActorClass), name);
	}
	
	public static <T, Y extends T> Y getTypedActor(ActorSystem system, Class<T> typedActorClass, Creator<Y> creator, String name) {
		return AchooActorSystem.getTypedActor(system, new TypedProps<Y>(typedActorClass, creator), name);
	}
	
	public static <T> T getTypedActor(ActorSystem system, TypedProps<T> typedProps) {
		return AchooActorSystem.getTypedActor(system, typedProps, (String)null);
	}
	
	public static <T> T getTypedActor(ActorSystem system, TypedProps<T> typedProps, String name) {

		T actor = null;
		
		if(name != null && !name.isEmpty()) {
			actor = TypedActor.get(system).typedActorOf(typedProps, name);
		} else {
			actor = TypedActor.get(system).typedActorOf(typedProps);
		}

		return actor;
	}
	
}
