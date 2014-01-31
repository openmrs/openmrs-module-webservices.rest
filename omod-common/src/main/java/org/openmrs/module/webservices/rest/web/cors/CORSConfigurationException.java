package org.openmrs.module.webservices.rest.web.cors;

/**
 * CORS filter configuration exception, intended to report invalid init parameters at startup.
 * 
 * @author Vladimir Dzhuvinov
 */
public class CORSConfigurationException extends Exception {
	
	/**
	 * Creates a new CORS filter configuration exception with the specified message.
	 * 
	 * @param message The exception message.
	 */
	public CORSConfigurationException(final String message) {
		
		super(message);
	}
	
	/**
	 * Creates a new CORS filter configuration exception with the specified message and cause.
	 * 
	 * @param message The exception message.
	 * @param cause The exception cause.
	 */
	public CORSConfigurationException(final String message, final Throwable cause) {
		
		super(message, cause);
	}
}
