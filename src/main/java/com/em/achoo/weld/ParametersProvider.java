package com.em.achoo.weld;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

import com.beust.jcommander.JCommander;
import com.em.achoo.configure.AchooCommandLine;

public class ParametersProvider {
	
	private String[] params = new String[0];
	
	public void establishParameters(@Observes ParametersEvent event) {
		this.params = event.getParameters();		
	}
	
	@Produces
	@AchooBootstrap
	public AchooCommandLine commandLineProducer() {
		AchooCommandLine commandValues = new AchooCommandLine();
		new JCommander(commandValues, this.params);
		return commandValues;
	}
	
}
