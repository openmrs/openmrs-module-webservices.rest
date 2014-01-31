package org.openmrs.module.webservices.rest.web.cors;

/**
 * Base Cross-Origin Resource Sharing (CORS) exception, typically thrown during processing of CORS
 * requests.
 * 
 * @author Vladimir Dzhuvinov
 */
public class CORSException extends Exception {
	
	/**
	 * Creates a new CORS exception with the specified message.
	 * 
	 * @param message The message.
	 */
	public CORSException(final String message) {
		
		super(message);
	}
}
