package org.openmrs.module.webservices.rest.web.cors;

import javax.servlet.http.HttpServletRequest;

/**
 * Request tagger. Tags HTTP servlet requests to provide CORS information to downstream handlers.
 * 
 * @author Vladimir Dzhuvinov
 */
public final class RequestTagger {
	
	/**
	 * Tags an HTTP servlet request to provide CORS information to downstream handlers.
	 * <p/>
	 * Tagging is provided via {@code HttpServletRequest.setAttribute()}.
	 * <ul>
	 * <li>{@code cors.isCorsRequest} set to {@code true} or {@code false}.
	 * <li>{@code cors.origin} set to the value of the "Origin" header, {@code null} if undefined.
	 * <li>{@code cors.requestType} set to "actual" or "preflight" (for CORS requests).
	 * <li>{@code cors.requestHeaders} set to the value of the "Access-Control-Request-Headers" or
	 * {@code null} if undefined (added for preflight CORS requests only).
	 * </ul>
	 * 
	 * @param request The servlet request to inspect and tag. Must not be {@code null}.
	 * @param type The detected request type. Must not be {@code null}.
	 */
	public static void tag(final HttpServletRequest request, final CORSRequestType type) {
		
		switch (type) {
		
			case ACTUAL:
				request.setAttribute("cors.isCorsRequest", true);
				request.setAttribute("cors.origin", request.getHeader("Origin"));
				request.setAttribute("cors.requestType", "actual");
				break;
			
			case PREFLIGHT:
				request.setAttribute("cors.isCorsRequest", true);
				request.setAttribute("cors.origin", request.getHeader("Origin"));
				request.setAttribute("cors.requestType", "preflight");
				request.setAttribute("cors.requestHeaders", request.getHeader("Access-Control-Request-Headers"));
				break;
			
			case OTHER:
				request.setAttribute("cors.isCorsRequest", false);
		}
	}
}
