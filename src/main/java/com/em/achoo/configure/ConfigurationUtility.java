package com.em.achoo.configure;

import java.io.File;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigurationUtility {

	private ConfigurationUtility() {
		
	}
	
	public static Config getConfiguration(String configFileResourceName) {
		return ConfigurationUtility.getConfiguration(null, configFileResourceName);
	}
	
	
	public static Config getConfiguration(File configFile, String configFileResourceName) {

		//if the file is null, check for default
		if(configFile == null) {
			configFile = new File("./" + configFileResourceName + ".conf");
		}
		
		//parse found file, if it is found
		Config achooConfig = null;
		if(configFile.exists() && configFile.isFile()) {
			achooConfig = ConfigFactory.parseFile(configFile);
		} else {
			achooConfig = ConfigFactory.empty();
		}
		
		//merge with fallback onto built-in configuration file and akka defaults
		Config resourceConfig = ConfigFactory.load(configFileResourceName);
		achooConfig = achooConfig.withFallback(resourceConfig).withFallback(ConfigFactory.load());
		
		return achooConfig;		
	}
	
}
