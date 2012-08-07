package com.em.achoo.actors.router;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import scala.collection.JavaConversions;
import akka.actor.Props;
import akka.routing.Resizer;
import akka.routing.RouteeProvider;

public class DynamicResizer implements Resizer {
	
	//private Logger logger = LoggerFactory.getLogger(DynamicResizer.class);

	@Override
	public boolean isTimeForResize(long arg0) {
		return true;
	}

	@Override
	public void resize(Props arg0, RouteeProvider arg1) {
		List<String> names = new ArrayList<String>(1);
		names.add(arg0._2() + "-" + UUID.randomUUID().toString().toUpperCase());
		arg1.createAndRegisterRoutees(arg0, 1, JavaConversions.asScalaIterable(names));
	}

}
