/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.validation.ValidationException;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BaseRestControllerTest extends BaseModuleWebContextSensitiveTest {
	
	BaseRestController controller;
	
	MockHttpServletRequest request;
	
	MockHttpServletResponse response;
	
	@Before
	public void before() {
		controller = new BaseRestController();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}
	
	/**
	 * @verifies return unauthorized if not logged in
	 * @see BaseRestController#apiAuthenticationExceptionHandler(Exception,
	 *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Test
	public void apiAuthenticationExceptionHandler_shouldReturnNotFoundIfNotLoggedIn() throws Exception {
		Context.logout();
		
		controller.apiAuthenticationExceptionHandler(new APIAuthenticationException(), request, response);
		
		assertThat(response.getStatus(), is(HttpServletResponse.SC_NOT_FOUND));
	}
	
	/**
	 * @verifies return forbidden if logged in
	 * @see BaseRestController#apiAuthenticationExceptionHandler(Exception,
	 *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Test
	public void apiAuthenticationExceptionHandler_shouldReturnForbiddenIfLoggedIn() throws Exception {
		controller.apiAuthenticationExceptionHandler(new APIAuthenticationException(), request, response);
		
		assertThat(response.getStatus(), is(HttpServletResponse.SC_FORBIDDEN));
	}
	
	@Test
	public void validationException_shouldReturnBadRequestResponse() throws Exception {
		Errors ex = new BindException(new Person(), "");
		ex.reject("error.message");
		
		SimpleObject responseSimpleObject = controller.validationExceptionHandler(new ValidationException(ex), request,
		    response);
		assertThat(response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
		
		SimpleObject errors = (SimpleObject) responseSimpleObject.get("error");
		Assert.assertEquals("webservices.rest.error.invalid.submission", errors.get("message"));
	}
}
