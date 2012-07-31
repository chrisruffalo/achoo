package com.em.achoo.configure;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

public class AchooCommandLine {

	@Parameter(names={"-c","--config"}, converter=FileConverter.class)
	private File configurationFile = null;

	@Parameter(names={"-?", "-h", "--help"}, help=true)
	private boolean help = false;
	
	public File getConfigurationFile() {
		return configurationFile;
	}

	public void setConfigurationFile(File configurationFile) {
		this.configurationFile = configurationFile;
	}

	public boolean isHelp() {
		return help;
	}

	public void setHelp(boolean help) {
		this.help = help;
	}
	
}
