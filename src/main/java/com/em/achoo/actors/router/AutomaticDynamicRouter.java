package com.em.achoo.actors.router;

import scala.Option;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.SupervisorStrategy;
import akka.routing.RoutedActorCell;
import akka.routing.Router;

public class AutomaticDynamicRouter implements Router {

	private Router delegate = null;
	
	public AutomaticDynamicRouter(Router implementingRouter) {
		this.delegate = implementingRouter;
	}
	
	@Override
	public void akka$actor$Actor$_setter_$context_$eq(ActorContext arg0) {
		this.delegate.akka$actor$Actor$_setter_$context_$eq(arg0);
	}

	@Override
	public void akka$actor$Actor$_setter_$self_$eq(ActorRef arg0) {
		this.delegate.akka$actor$Actor$_setter_$self_$eq(arg0);
	}

	@Override
	public ActorContext context() {
		return this.delegate.context();
	}

	@Override
	public void postRestart(Throwable arg0) {
		this.delegate.postRestart(arg0);
	}

	@Override
	public void postStop() {
		this.delegate.postStop();
	}

	@Override
	public void preStart() {
		this.delegate.preStart();		
	}

	@Override
	public ActorRef self() {
		return this.delegate.self();
	}

	@Override
	public ActorRef sender() {
		return this.delegate.sender();
	}

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return this.delegate.supervisorStrategy();
	}

	@Override
	public void unhandled(Object arg0) {
		this.delegate.unhandled(arg0);
	}

	@Override
	public void akka$routing$Router$_setter_$ref_$eq(RoutedActorCell arg0) {
		this.delegate.akka$routing$Router$_setter_$ref_$eq(arg0);
		
	}

	@Override
	public void preRestart(Throwable arg0, Option<Object> arg1) {
		this.delegate.preRestart(arg0, arg1);
	}

	@Override
	public PartialFunction<Object, BoxedUnit> receive() {
		SubscriberUpdatePartialFunction subscriberUpdate = new SubscriberUpdatePartialFunction(this);
		return subscriberUpdate.orElse(this.delegate.receive());
	}

	@Override
	public RoutedActorCell ref() {
		return this.delegate.ref();
	}

	@Override
	public PartialFunction<Object, BoxedUnit> routerReceive() {
		return this.delegate.receive();
	}

}
