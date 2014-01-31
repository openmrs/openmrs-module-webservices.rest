package org.openmrs.module.webservices.rest.web.cors;

/**
 * Origin exception.
 * 
 * @author Vladimir Dzhuvinov
 */
public class OriginException extends Exception {
	
	/**
	 * Creates a new origin exception with the specified message.
	 * 
	 * @param message The message.
	 */
	public OriginException(final String message) {
		
		super(message);
	}
}
