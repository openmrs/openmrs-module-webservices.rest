package org.openmrs.module.webservices.rest.web.cors;

/**
 * Enumeration of supported standard HTTP methods.
 * 
 * @author Vladimir Dzhuvinov
 */
public enum HTTPMethod {
	
	/**
	 * HTTP GET.
	 */
	GET,
	
	/**
	 * HTTP POST.
	 */
	POST,
	
	/**
	 * HTTP HEAD.
	 */
	HEAD,
	
	/**
	 * HTTP PUT.
	 */
	PUT,
	
	/**
	 * HTTP DELETE.
	 */
	DELETE,
	
	/**
	 * HTTP TRACE.
	 */
	TRACE,
	
	/**
	 * HTTP OPTIONS.
	 */
	OPTIONS,
	
	/**
	 * HTTP CONNECT.
	 */
	CONNECT,
	
	/**
	 * HTTP PATCH.
	 */
	PATCH
}
