/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Generic request filter intended for all /ws/rest calls <br/>
 */
public class RequestFilter implements Filter {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.debug("Initializing REST WS generic request filter");
	}
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
	        throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		response.addHeader("Access-Control-Allow-Origin", "*");
		
		// continue with the filter chain in all circumstances
		filterChain.doFilter(servletRequest, response);
	}
	
	@Override
	public void destroy() {
		log.debug("Destroying REST WS generic request filter");
	}
}
