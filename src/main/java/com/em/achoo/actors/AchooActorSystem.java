package com.em.achoo.actors;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;

public enum AchooActorSystem {

	INSTANCE;
	
	private static final String ACHOO_ACTOR_SYSTEM_NAME = "achoo";
	
	private ActorSystem system = null;
	
	private AchooActorSystem() {
		this.system = ActorSystem.create(AchooActorSystem.ACHOO_ACTOR_SYSTEM_NAME);
	}
	
	public ActorSystem getSystem() {
		return this.system;
	}
	
	public <T> T getTypedActor(Class<T> typedActorClass) {
		return this.getTypedActor(new TypedProps<T>(typedActorClass));
	}
	
	public <T,Y extends T> T getTypedActor(Class<T> typedActorClass, Class<Y> implementingClass) {
		return this.getTypedActor(new TypedProps<Y>(typedActorClass, implementingClass));
	}

	public <T, Y extends T> Y getTypedActor(Class<T> typedActorClass, Creator<Y> creator) {
		return this.getTypedActor(new TypedProps<Y>(typedActorClass, creator));
	}
	
	public <T> T getTypedActor(Class<T> typedActorClass, String name) {
		return this.getTypedActor(new TypedProps<T>(typedActorClass), name);
	}
	
	public <T, Y extends T> Y getTypedActor(Class<T> typedActorClass, Creator<Y> creator, String name) {
		return this.getTypedActor(new TypedProps<Y>(typedActorClass, creator), name);
	}
	
	public <T> T getTypedActor(TypedProps<T> typedProps) {
		return this.getTypedActor(typedProps, (String)null);
	}
	
	public <T> T getTypedActor(TypedProps<T> typedProps, String name) {

		ActorSystem system = this.getSystem();
		T actor = null;
		
		if(name != null && !name.isEmpty()) {
			actor = TypedActor.get(system).typedActorOf(typedProps, name);
		} else {
			actor = TypedActor.get(system).typedActorOf(typedProps);
		}

		return actor;
	}
	
}
