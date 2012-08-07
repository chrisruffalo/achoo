package com.em.achoo.actors.router;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import scala.Option;
import scala.PartialFunction;
import scala.Tuple2;
import scala.collection.Iterable;
import scala.collection.JavaConversions;
import scala.runtime.AbstractPartialFunction;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.routing.Destination;
import akka.routing.Resizer;
import akka.routing.RouteeProvider;
import akka.routing.Router;
import akka.routing.RouterConfig;

import com.em.achoo.model.Subscription;

public class AutomaticDynamicRouterConfig implements RouterConfig {

	private RouterConfig delegate = null;
		
	public AutomaticDynamicRouterConfig(RouterConfig implementingRouterConfig) {
		this.delegate = implementingRouterConfig;
	}
	
	@Override
	public Router createActor() {
		return new AutomaticDynamicRouter(this.delegate.createActor());
	}

	@Override
	public PartialFunction<Tuple2<ActorRef, Object>, Iterable<Destination>> createRoute(final Props arg0, final RouteeProvider arg1) {
		AbstractPartialFunction<Tuple2<ActorRef, Object>, Iterable<Destination>> reRoutingFunction = new AbstractPartialFunction<Tuple2<ActorRef,Object>, Iterable<Destination>>() {

			@Override
			public boolean isDefinedAt(Tuple2<ActorRef, Object> tuple) {
				ActorRef ref = tuple._1();
				Object value = tuple._2();

				LoggerFactory.getLogger(this.getClass()).debug("Checking defined for internal route ({} - {}, {})", new Object[]{ref.path().toString(), ref.getClass().getName(), value.getClass().toString()});

			
				boolean result = false;
				
				//route subscription to somewhere else
				if(value instanceof Subscription) {
					result = true;
				}
				
				LoggerFactory.getLogger(this.getClass()).debug("Result: {}", Boolean.toString(result));
				
				return result;
			}

			@Override
			public Iterable<Destination> apply(Tuple2<ActorRef, Object> tuple) {
				ActorRef ref = tuple._1();
				Object value = tuple._2();

				LoggerFactory.getLogger(this.getClass()).debug("Checking apply ({} - {}, {})", new Object[]{ref.path().toString(), ref.getClass().getName(), value.getClass().toString()});
				
				Iterable<Destination> result = null;
				
				if(value instanceof Subscription) {
					List<Destination> destinations = new ArrayList<Destination>();
					destinations.add(new Destination(ref, arg1.context().self()));
					result = JavaConversions.asScalaIterable(destinations);
				} 	
				
				return result;
			} 		
		
		};
		
		return reRoutingFunction.orElse(this.delegate.createRoute(arg0, arg1));
	}

	@Override
	public RouteeProvider createRouteeProvider(ActorContext arg0) {
		return this.delegate.createRouteeProvider(arg0);
	}

	@Override
	public Option<Resizer> resizer() {
		return this.delegate.resizer();
	}

	@Override
	public String routerDispatcher() {
		return this.delegate.routerDispatcher();
	}

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return this.delegate.supervisorStrategy();
	}

	@Override
	public Iterable<Destination> toAll(ActorRef arg0, Iterable<ActorRef> arg1) {
		return this.delegate.toAll(arg0, arg1);
	}

	@Override
	public void verifyConfig() {
		this.delegate.verifyConfig();
	}

	@Override
	public RouterConfig withFallback(RouterConfig arg0) {
		return this.delegate.withFallback(arg0);
	}

}
