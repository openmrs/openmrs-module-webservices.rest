package org.openmrs.module.webservices.rest.web.cors;

/**
 * Invalid CORS request exception. Thrown to indicate a CORS request (simple / actual or preflight)
 * that doesn't conform to the specification.
 * 
 * @author Vladimir Dzhuvinov
 */
public class InvalidCORSRequestException extends CORSException {
	
	/**
	 * Creates a new invalid CORS request exception with the specified message.
	 * 
	 * @param message The message.
	 */
	public InvalidCORSRequestException(final String message) {
		
		super(message);
	}
}
