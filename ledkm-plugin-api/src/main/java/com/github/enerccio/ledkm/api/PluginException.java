package com.github.enerccio.ledkm.api;

public class PluginException extends RuntimeException {
	
	private static final long serialVersionUID = -927049451347542571L;

	public PluginException() {
		super();
	}

	public PluginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PluginException(String message, Throwable cause) {
		super(message, cause);
	}

	public PluginException(String message) {
		super(message);
	}

	public PluginException(Throwable cause) {
		super(cause);
	}

	
	
}
