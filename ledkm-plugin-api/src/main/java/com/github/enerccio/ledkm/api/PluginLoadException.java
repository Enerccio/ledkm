package com.github.enerccio.ledkm.api;

public class PluginLoadException extends Exception {

	private static final long serialVersionUID = -3406091786934502681L;

	public PluginLoadException() {
		super();
	}

	public PluginLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PluginLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public PluginLoadException(String message) {
		super(message);
	}

	public PluginLoadException(Throwable cause) {
		super(cause);
	}

	
	
}
