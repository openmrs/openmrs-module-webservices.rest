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

import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests for the {@link AuthorizationFilter} class.
 */
public class AuthorizationFilterTest extends BaseModuleWebContextSensitiveTest {
	
	private AuthorizationFilter filter;
	
	private MockFilterChain chain;
	
	private MockHttpServletRequest request;
	
	private MockHttpServletResponse response;
	
	@BeforeEach
	public void setup() {
		filter = new AuthorizationFilter();
		chain = new MockFilterChain();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		request.setRequestedSessionId("stale-session-id");
		request.setRequestedSessionIdValid(false);
	}
	
	@Test
	public void doFilter_shouldReturnUnauthorizedForStaleSessionOnOtherEndpoints() throws Exception {
		request.setRequestURI("/ws/rest/v1/patient");
		
		filter.doFilter(request, response, chain);
		
		Assertions.assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
	}
	
	@Test
	public void doFilter_shouldNotReturnUnauthorizedForStaleSessionOnSessionEndpoint() throws Exception {
		request.setRequestURI("/ws/rest/v1/session");
		
		filter.doFilter(request, response, chain);
		
		Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		Assertions.assertNotNull(chain.getRequest());
	}
	
	@Test
	public void doFilter_shouldNotReturnUnauthorizedForStaleSessionOnSessionEndpointWithContextPath() throws Exception {
		request.setContextPath("/openmrs");
		request.setRequestURI("/openmrs/ws/rest/v1/session/");
		
		filter.doFilter(request, response, chain);
		
		Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		Assertions.assertNotNull(chain.getRequest());
	}
}
