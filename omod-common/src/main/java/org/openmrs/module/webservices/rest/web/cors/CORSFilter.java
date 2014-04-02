package org.openmrs.module.webservices.rest.web.cors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Cross-Origin Resource Sharing (CORS) servlet filter.
 * <p/>
 * The filter intercepts incoming HTTP requests and applies the CORS policy as specified by the
 * filter init parameters. The actual CORS request is processed by the {@link CORSRequestHandler}
 * class.
 * <p/>
 * Supported configuration parameters:
 * <ul>
 * <li>cors.allowGenericHttpRequests {true|false} defaults to {@code true}.
 * <li>cors.allowOrigin {"*"|origin-list} defaults to {@code *}.
 * <li>cors.allowSubdomains {true|false} defaults to {@code false}.
 * <li>cors.supportedMethods {method-list} defaults to {@code "GET, POST, HEAD, OPTIONS"}.
 * <li>cors.supportedHeaders {"*"|header-list} defaults to {@code *}.
 * <li>cors.exposedHeaders {header-list} defaults to empty list.
 * <li>cors.supportsCredentials {true|false} defaults to {@code true}.
 * <li>cors.maxAge {int} defaults to {@code -1} (unspecified).
 * <li>cors.tagRequests {boolean} default to {@code false}.
 * </ul>
 * 
 * @author Vladimir Dzhuvinov
 * @author David Bellem
 */
public class CORSFilter implements Filter {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * The CORS filer configuration.
	 */
	private CORSConfiguration config;
	
	/**
	 * Encapsulates the CORS request handling logic.
	 */
	private CORSRequestHandler handler;
	
	/**
	 * Gets the configuration of this CORS filter.
	 * 
	 * @return The configuration, {@code null} if the filter is not initialised.
	 */
	public CORSConfiguration getConfiguration() {
		
		return config;
	}
	
	/**
	 * Sets the configuration of this CORS filter.
	 */
	public void setConfiguration(CORSConfiguration corsConfiguration) {
		this.config = corsConfiguration;
		this.handler = new CORSRequestHandler(config);
	}
	
	/**
	 * This method is invoked by the web container to initialise the filter at startup.
	 * 
	 * @param filterConfig The servlet filter configuration. Must not be {@code null}.
	 * @throws javax.servlet.ServletException On a filter initialisation exception.
	 */
	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		
		log.debug("Initializing CORS Filter");
		CORSConfigurationLoader configLoader = new CORSConfigurationLoader(filterConfig);
		
		try {
			config = configLoader.load();
			
		}
		catch (CORSConfigurationException e) {
			
			throw new ServletException(e.getMessage(), e);
		}
		
		handler = new CORSRequestHandler(config);
	}
	
	/**
	 * Produces a simple HTTP text/plain response with the specified status code and message.
	 * <p/>
	 * Note: The CORS filter avoids falling back to the default web container error page (typically
	 * a richly-formatted HTML page) to make it easier for XHR debugger tools to identify the cause
	 * of failed requests.
	 * 
	 * @param sc The HTTP status code.
	 * @param msg The message.
	 * @throws java.io.IOException On a I/O exception.
	 * @throws javax.servlet.ServletException On a general request processing exception.
	 */
	private void printMessage(final HttpServletResponse response, final int sc, final String msg) throws IOException,
	        ServletException {
		
		// Set the status code
		response.setStatus(sc);
		
		// Write the error message
		
		response.resetBuffer();
		
		response.setContentType("text/plain");
		
		PrintWriter out = response.getWriter();
		
		out.println("Cross-Origin Resource Sharing (CORS) Filter: " + msg);
	}
	
	/**
	 * Filters an HTTP request / response pair according to the configured CORS policy. Also tags
	 * the request with CORS information to downstream handlers.
	 * 
	 * @param request The servlet request.
	 * @param response The servlet response.
	 * @param chain The servlet filter chain.
	 * @throws java.io.IOException On a I/O exception.
	 * @throws javax.servlet.ServletException On a general request processing exception.
	 */
	private void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
	        throws IOException, ServletException {
		
		CORSRequestType type = CORSRequestType.detect(request);
		
		// Tag if configured
		if (config.tagRequests)
			RequestTagger.tag(request, type);
		
		try {
			if (type.equals(CORSRequestType.ACTUAL)) {
				
				// Simple / actual CORS request
				handler.handleActualRequest(request, response);
				chain.doFilter(request, response);
				
			} else if (type.equals(CORSRequestType.PREFLIGHT)) {
				
				// Preflight CORS request, handle but don't 
				// pass further down the chain
				handler.handlePreflightRequest(request, response);
				
			} else if (config.allowGenericHttpRequests) {
				
				// Not a CORS request, but allow it through
				chain.doFilter(request, response);
				
			} else {
				
				// Generic HTTP requests denied
				printMessage(response, HttpServletResponse.SC_FORBIDDEN, "Generic HTTP requests not allowed");
			}
			
		}
		catch (InvalidCORSRequestException e) {
			
			printMessage(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			
		}
		catch (CORSOriginDeniedException e) {
			
			String msg = e.getMessage() + ": " + e.getRequestOrigin();
			printMessage(response, HttpServletResponse.SC_FORBIDDEN, msg);
			
		}
		catch (UnsupportedHTTPMethodException e) {
			
			String msg = e.getMessage();
			
			HTTPMethod method = e.getRequestedMethod();
			
			if (method != null)
				msg = msg + ": " + method.toString();
			
			printMessage(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
			
		}
		catch (UnsupportedHTTPHeaderException e) {
			
			String msg = e.getMessage();
			
			HeaderFieldName header = e.getRequestHeader();
			
			if (header != null)
				msg = msg + ": " + header.toString();
			
			printMessage(response, HttpServletResponse.SC_FORBIDDEN, msg);
		}
	}
	
	/**
	 * Called by the servlet container each time a request / response pair is passed through the
	 * chain due to a client request for a resource at the end of the chain.
	 * 
	 * @param request The servlet request.
	 * @param response The servlet response.
	 * @param chain The servlet filter chain.
	 * @throws java.io.IOException On a I/O exception.
	 * @throws javax.servlet.ServletException On a general request processing exception.
	 */
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
	        throws IOException, ServletException {
		
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			
			// Cast to HTTP
			doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
			
		} else {
			
			throw new ServletException("Cannot filter non-HTTP requests/responses");
		}
	}
	
	/**
	 * Called by the web container to indicate to a filter that it is being taken out of service.
	 */
	@Override
	public void destroy() {
		log.debug("Destroying CORS Filter");
		// do nothing
	}
}
