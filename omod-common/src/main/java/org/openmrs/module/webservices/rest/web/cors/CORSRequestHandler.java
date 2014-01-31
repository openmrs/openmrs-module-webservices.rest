package org.openmrs.module.webservices.rest.web.cors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles incoming cross-origin (CORS) requests according to the configured access policy.
 * Encapsulates the CORS processing logic as specified by the <a
 * href="http://www.w3.org/TR/2013/CR-cors-20130129/">W3C candidate recommendation</a> from
 * 2013-01-29.
 * <p/>
 * Note that the actual CORS exception handling (which is outside the CORS specification scope) is
 * left to the invoking class to implement.
 * 
 * @author Vladimir Dzhuvinov
 */
public class CORSRequestHandler {
	
	/**
	 * The CORS filter configuration, detailing the cross-origin access policy.
	 */
	private final CORSConfiguration config;
	
	/**
	 * Pre-computed string of the CORS supported methods.
	 */
	private final String supportedMethods;
	
	/**
	 * Pre-computed string of the CORS supported headers.
	 */
	private final String supportedHeaders;
	
	/**
	 * Pre-computed string of the CORS exposed headers.
	 */
	private final String exposedHeaders;
	
	/**
	 * Creates a new CORS request handler.
	 * 
	 * @param config Specifies the cross-origin access policy.
	 */
	public CORSRequestHandler(final CORSConfiguration config) {
		
		this.config = config;
		
		// Pre-compute response headers where possible
		
		// Access-Control-Allow-Methods
		supportedMethods = HeaderUtils.serialize(config.supportedMethods, ", ");
		
		// Access-Control-Allow-Headers
		if (!config.supportAnyHeader)
			supportedHeaders = HeaderUtils.serialize(config.supportedHeaders, ", ");
		else
			supportedHeaders = null;
		
		/// Access-Control-Expose-Headers
		exposedHeaders = HeaderUtils.serialize(config.exposedHeaders, ", ");
	}
	
	/**
	 * Handles a simple or actual CORS request.
	 * <p/>
	 * CORS specification: <a
	 * href="http://www.w3.org/TR/2013/CR-cors-20130129/#resource-requests">Simple Cross-Origin
	 * Request, Actual Request, and Redirects</a>
	 * 
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @throws InvalidCORSRequestException If not a valid CORS simple / actual request.
	 * @throws CORSOriginDeniedException If the origin is not allowed.
	 * @throws UnsupportedHTTPMethodException If the requested HTTP method is not supported by the
	 *             CORS policy.
	 */
	public void handleActualRequest(final HttpServletRequest request, final HttpServletResponse response)
	        throws InvalidCORSRequestException, CORSOriginDeniedException, UnsupportedHTTPMethodException {
		
		if (CORSRequestType.detect(request) != CORSRequestType.ACTUAL)
			throw new InvalidCORSRequestException("Invalid simple/actual CORS request");
		
		// Check origin against allow list
		Origin requestOrigin = new Origin(request.getHeader("Origin"));
		
		if (!config.isAllowedOrigin(requestOrigin))
			throw new CORSOriginDeniedException("CORS origin denied", requestOrigin);
		
		// Check method
		
		HTTPMethod method;
		
		try {
			method = HTTPMethod.valueOf(request.getMethod());
			
		}
		catch (Exception e) {
			// Parse exception
			throw new UnsupportedHTTPMethodException("Unsupported HTTP method: " + request.getMethod());
		}
		
		if (!config.isSupportedMethod(method))
			throw new UnsupportedHTTPMethodException("Unsupported HTTP method", method);
		
		// Success, append response headers
		if (config.supportsCredentials) {
			
			response.addHeader("Access-Control-Allow-Credentials", "true");
			
			// The string "*" cannot be used for a resource that supports credentials.
			response.addHeader("Access-Control-Allow-Origin", requestOrigin.toString());
			
			// See https://bitbucket.org/thetransactioncompany/cors-filter/issue/16/
			response.addHeader("Vary", "Origin");
			
		} else {
			if (config.allowAnyOrigin) {
				response.addHeader("Access-Control-Allow-Origin", "*");
			} else {
				response.addHeader("Access-Control-Allow-Origin", requestOrigin.toString());
				
				// See https://bitbucket.org/thetransactioncompany/cors-filter/issue/16/
				response.addHeader("Vary", "Origin");
			}
		}
		
		if (!exposedHeaders.isEmpty())
			response.addHeader("Access-Control-Expose-Headers", exposedHeaders);
	}
	
	/**
	 * Handles a preflight CORS request.
	 * <p/>
	 * CORS specification: <a
	 * href="http://www.w3.org/TR/2013/CR-cors-20130129/#resource-preflight-requests">Preflight
	 * Request</a>
	 * 
	 * @param request The HTTP request.
	 * @param response The HTTP response.
	 * @throws InvalidCORSRequestException If not a valid CORS preflight request.
	 * @throws CORSOriginDeniedException If the origin is not allowed.
	 * @throws UnsupportedHTTPMethodException If the requested HTTP method is not supported by the
	 *             CORS policy.
	 * @throws UnsupportedHTTPHeaderException If the requested HTTP header is not supported by the
	 *             CORS policy.
	 */
	public void handlePreflightRequest(final HttpServletRequest request, final HttpServletResponse response)
	        throws InvalidCORSRequestException, CORSOriginDeniedException, UnsupportedHTTPMethodException,
	        UnsupportedHTTPHeaderException {
		
		if (CORSRequestType.detect(request) != CORSRequestType.PREFLIGHT)
			throw new InvalidCORSRequestException("Invalid preflight CORS request");
		
		// Check origin against allow list
		Origin requestOrigin = new Origin(request.getHeader("Origin"));
		
		if (!config.isAllowedOrigin(requestOrigin))
			throw new CORSOriginDeniedException("CORS origin denied", requestOrigin);
		
		// Parse requested method
		// Note: method checking must be done after header parsing, see CORS spec
		
		String requestMethodHeader = request.getHeader("Access-Control-Request-Method");
		
		if (requestMethodHeader == null)
			throw new InvalidCORSRequestException(
			        "Invalid preflight CORS request: Missing Access-Control-Request-Method header");
		
		HTTPMethod requestedMethod;
		
		try {
			requestedMethod = HTTPMethod.valueOf(requestMethodHeader.toUpperCase());
			
		}
		catch (Exception e) {
			// Parse exception
			throw new UnsupportedHTTPMethodException("Unsupported HTTP method: " + requestMethodHeader);
		}
		
		// Parse the requested author (custom) headers
		final String rawRequestHeadersString = request.getHeader("Access-Control-Request-Headers");
		final String[] requestHeaderValues = HeaderUtils.parseMultipleHeaderValues(rawRequestHeadersString);
		
		final HeaderFieldName[] requestHeaders = new HeaderFieldName[requestHeaderValues.length];
		
		for (int i = 0; i < requestHeaders.length; i++) {
			
			try {
				requestHeaders[i] = new HeaderFieldName(requestHeaderValues[i]);
				
			}
			catch (IllegalArgumentException e) {
				// Invalid header name
				throw new InvalidCORSRequestException("Invalid preflight CORS request: Bad request header value");
			}
		}
		
		// Now, do method check
		if (!config.isSupportedMethod(requestedMethod))
			throw new UnsupportedHTTPMethodException("Unsupported HTTP method", requestedMethod);
		
		// Author request headers check
		if (!config.supportAnyHeader) {
			
			for (HeaderFieldName requestHeader : requestHeaders) {
				
				if (!config.supportedHeaders.contains(requestHeader))
					throw new UnsupportedHTTPHeaderException("Unsupported HTTP request header", requestHeader);
			}
		}
		
		// Success, append response headers
		
		if (config.supportsCredentials) {
			response.addHeader("Access-Control-Allow-Origin", requestOrigin.toString());
			response.addHeader("Access-Control-Allow-Credentials", "true");
			
			// See https://bitbucket.org/thetransactioncompany/cors-filter/issue/16/
			response.addHeader("Vary", "Origin");
		} else {
			if (config.allowAnyOrigin) {
				response.addHeader("Access-Control-Allow-Origin", "*");
			} else {
				response.addHeader("Access-Control-Allow-Origin", requestOrigin.toString());
				
				// See https://bitbucket.org/thetransactioncompany/cors-filter/issue/16/
				response.addHeader("Vary", "Origin");
			}
		}
		
		if (config.maxAge > 0)
			response.addHeader("Access-Control-Max-Age", Integer.toString(config.maxAge));
		
		response.addHeader("Access-Control-Allow-Methods", supportedMethods);
		
		if (config.supportAnyHeader && rawRequestHeadersString != null) {
			
			// Echo author headers
			response.addHeader("Access-Control-Allow-Headers", rawRequestHeadersString);
			
		} else if (supportedHeaders != null && !supportedHeaders.isEmpty()) {
			
			response.addHeader("Access-Control-Allow-Headers", supportedHeaders);
		}
	}
}
