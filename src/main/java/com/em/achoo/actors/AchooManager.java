package com.em.achoo.actors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.PoisonPill;
import akka.actor.UntypedActor;

import com.em.achoo.Achoo;
import com.em.achoo.model.management.StartMessage;
import com.em.achoo.model.management.StopMessage;

public class AchooManager extends UntypedActor {

	public static final String NAME = "achoo-manager";
	
	protected Logger log = LoggerFactory.getLogger(Achoo.class);
	
	protected Achoo achoo = null;
	
	public AchooManager(Achoo toManage) {
		this.achoo = toManage;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {

		if(this.achoo == null) {
			return;
		}
		
		if(message instanceof StopMessage) {
			this.log.info("Achoo recieved stop message.");
			this.achoo.stop();
			this.self().tell(PoisonPill.getInstance());
		} else if(message instanceof StartMessage){
			this.log.info("Achoo recieved start message.");
			this.achoo.start((StartMessage)message);
		}
	}
	
}
