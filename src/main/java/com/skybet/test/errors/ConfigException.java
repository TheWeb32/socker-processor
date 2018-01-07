package com.skybet.test.errors;

public class ConfigException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6133119440874865172L;

	public ConfigException(String message) {
		super(message);
	}
	
    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
