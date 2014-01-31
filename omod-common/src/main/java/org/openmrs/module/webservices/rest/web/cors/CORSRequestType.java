package org.openmrs.module.webservices.rest.web.cors;

import javax.servlet.http.HttpServletRequest;

/**
 * Enumeration of the CORS request types.
 * 
 * @author Vladimir Dzhuvinov
 * @author Brandon Murray
 */
public enum CORSRequestType {
	
	/**
	 * Simple / actual CORS request.
	 */
	ACTUAL,
	
	/**
	 * Preflight CORS request.
	 */
	PREFLIGHT,
	
	/**
	 * Other (non-CORS) request.
	 */
	OTHER;
	
	/**
	 * Detects the CORS type of the specified HTTP request.
	 * 
	 * @param request The HTTP request to check. Must not be {@code null}.
	 * @return The CORS request type.
	 */
	public static CORSRequestType detect(final HttpServletRequest request) {
		
		// All CORS request have an Origin header
		if (request.getHeader("Origin") == null)
			return OTHER;
		
		// Some browsers include the Origin header even when submitting 
		// from the same domain. This is legal according to RFC 6454, 
		// section-7.3
		String serverOrigin = request.getScheme() + "://" + request.getHeader("Host");
		
		if (request.getHeader("Host") != null && request.getHeader("Origin").equals(serverOrigin))
			return OTHER;
		
		// We have a CORS request - determine type
		if (request.getHeader("Access-Control-Request-Method") != null && request.getMethod() != null
		        && request.getMethod().equals("OPTIONS"))
			
			return PREFLIGHT;
		
		else
			return ACTUAL;
	}
}
