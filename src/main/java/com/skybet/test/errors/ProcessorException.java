package com.skybet.test.errors;

public class ProcessorException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6133119440874865172L;

	public ProcessorException(String message) {
		super(message);
	}
	
    public ProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

}
