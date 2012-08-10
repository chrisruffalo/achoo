package com.em.achoo.actors.management;

import com.em.achoo.Achoo;

import akka.actor.UntypedActor;

public class AchooManager extends UntypedActor {

	@Override
	public void onReceive(Object arg0) throws Exception {
		if(arg0 instanceof Achoo) {
			((Achoo)arg0).stop();
		}
	}
	
}
