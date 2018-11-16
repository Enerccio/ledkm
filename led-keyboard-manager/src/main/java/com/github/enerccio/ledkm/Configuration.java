package com.github.enerccio.ledkm;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Configuration {
	
	private static Path pluginDirectory = Paths.get("plugins");

	public static Path getPluginDirectory() {
		return pluginDirectory;
	}

	public static void setPluginDirectory(Path pluginDirectory) {
		Configuration.pluginDirectory = pluginDirectory;
	}

}
