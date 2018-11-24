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

	private static Path profileDirectory = Paths.get(System.getProperty("user.home"), ".ledkm", "profiles");
	
	public static Path getProfileDirectory() {
		return profileDirectory;
	}

	public static void setProfileDirectory(Path profileDirectory) {
		Configuration.profileDirectory = profileDirectory;
	}
}
