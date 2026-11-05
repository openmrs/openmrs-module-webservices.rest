/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Person;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.response.GenericRestException;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.openmrs.api.ValidationException;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import java.util.LinkedHashMap;

public class BaseRestControllerTest extends BaseModuleWebContextSensitiveTest {
	
	BaseRestController controller;
	
	MockHttpServletRequest request;
	
	MockHttpServletResponse response;
	
	Log spyOnLog;

	@BeforeEach
	public void before() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		controller = new BaseRestController();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		spyOnLog = mock(Log.class);
		// Need to get the logger using reflection
		Field log;
		log = controller.getClass().getDeclaredField("log");
		log.setAccessible(true);
		log.set(controller, spyOnLog);
	}
	
	/**
	 * @verifies return unauthorized if not logged in
	 * @see BaseRestController#apiAuthenticationExceptionHandler(Exception,
	 *      jakarta.servlet.http.HttpServletRequest, HttpServletResponse)
	 */
	@Test
	public void apiAuthenticationExceptionHandler_shouldReturnUnauthorizedIfNotLoggedIn() throws Exception {
		Context.logout();
		
		controller.apiAuthenticationExceptionHandler(new APIAuthenticationException(), request, response);
		
		assertThat(response.getStatus(), is(HttpServletResponse.SC_UNAUTHORIZED));
	}
	
	/**
	 * @verifies return forbidden if logged in
	 * @see BaseRestController#apiAuthenticationExceptionHandler(Exception,
	 *      jakarta.servlet.http.HttpServletRequest, HttpServletResponse)
	 */
	@Test
	public void apiAuthenticationExceptionHandler_shouldReturnForbiddenIfLoggedIn() throws Exception {
		controller.apiAuthenticationExceptionHandler(new APIAuthenticationException(), request, response);
		
		assertThat(response.getStatus(), is(HttpServletResponse.SC_FORBIDDEN));
	}
	
	/**
	 * @verifies delegate AccessDeniedException to the same 401/403 handling as
	 *           APIAuthenticationException
	 * @see BaseRestController#handleException(Exception, jakarta.servlet.http.HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Test
	public void handleException_shouldReturnUnauthorizedForAccessDeniedExceptionIfNotLoggedIn() throws Exception {
		Context.logout();

		controller.handleException(new AccessDeniedException("Denied"), request, response);

		assertThat(response.getStatus(), is(HttpServletResponse.SC_UNAUTHORIZED));
	}

	@Test
	public void handleException_shouldReturnForbiddenForAccessDeniedExceptionIfLoggedIn() throws Exception {
		controller.handleException(new AccessDeniedException("Denied"), request, response);

		assertThat(response.getStatus(), is(HttpServletResponse.SC_FORBIDDEN));
	}

	/**
	 * The subclass core's method security actually throws; handled by the AccessDeniedException branch
	 * because RestUtil.hasCause matches subtypes.
	 */
	@Test
	public void handleException_shouldReturnUnauthorizedForAuthorizationDeniedExceptionIfNotLoggedIn() throws Exception {
		Context.logout();

		controller.handleException(new AuthorizationDeniedException("Denied"), request, response);

		assertThat(response.getStatus(), is(HttpServletResponse.SC_UNAUTHORIZED));
	}

	@Test
	public void handleException_shouldReturnForbiddenForAuthorizationDeniedExceptionIfLoggedIn() throws Exception {
		controller.handleException(new AuthorizationDeniedException("Denied"), request, response);

		assertThat(response.getStatus(), is(HttpServletResponse.SC_FORBIDDEN));
	}

	/**
	 * Spring picks the {@code @ExceptionHandler} mapped to the closest exception type, so a subclass's
	 * own RuntimeException handler outranks a branch of {@link BaseRestController#handleException}.
	 *
	 * @verifies return unauthorized for an AccessDeniedException in a subclass that handles
	 *           RuntimeException if not logged in
	 * @see BaseRestController#apiAuthenticationExceptionHandler(Exception,
	 *      jakarta.servlet.http.HttpServletRequest, HttpServletResponse)
	 */
	@Test
	public void apiAuthenticationExceptionHandler_shouldReturnUnauthorizedForAccessDeniedExceptionInASubclassHandlingRuntimeExceptionIfNotLoggedIn()
	        throws Exception {
		Context.logout();

		MockMvcBuilders.standaloneSetup(new RuntimeExceptionHandlingController()).build()
		        .perform(get("/rest/" + RestConstants.VERSION_1 + "/runtimeexceptionhandling"))
		        .andExpect(status().isUnauthorized()).andExpect(header().exists("WWW-Authenticate"));
	}

	/**
	 * @verifies return forbidden for an AccessDeniedException in a subclass that handles
	 *           RuntimeException if logged in
	 * @see BaseRestController#apiAuthenticationExceptionHandler(Exception,
	 *      jakarta.servlet.http.HttpServletRequest, HttpServletResponse)
	 */
	@Test
	public void apiAuthenticationExceptionHandler_shouldReturnForbiddenForAccessDeniedExceptionInASubclassHandlingRuntimeExceptionIfLoggedIn()
	        throws Exception {
		MockMvcBuilders.standaloneSetup(new RuntimeExceptionHandlingController()).build()
		        .perform(get("/rest/" + RestConstants.VERSION_1 + "/runtimeexceptionhandling"))
		        .andExpect(status().isForbidden());
	}

	@Test
	public void validationException_shouldReturnBadRequestResponse() throws Exception {
		Errors ex = new BindException(new Person(), "");
		ex.reject("error.message");
		
		SimpleObject responseSimpleObject = controller.validationExceptionHandler(new ValidationException(ex), request,
		    response);
		assertThat(response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
		
		SimpleObject errors = (SimpleObject) responseSimpleObject.get("error");
		Assertions.assertEquals("webservices.rest.error.invalid.submission", errors.get("code"));
	}
	
	@Test
	public void handleException_shouldLogUnannotatedAsErrors() throws Exception {
		
		String message = "ErrorMessage";
		Exception ex = new Exception(message);
		controller.handleException(ex, request, response);

		verify(spyOnLog).error(message, ex);

	}
	
	@Test
	public void handleException_shouldLog500AndAboveAsErrors() throws Exception {
		String message = "ErrorMessage";
		Exception ex = new GenericRestException(message);
		
		controller.handleException(ex, request, response);

		verify(spyOnLog).error(message, ex);

	}
	
	@Test
	public void handleException_shouldLogBelow500AsInfo() throws Exception {
		
		String message = "ErrorMessage";
		Exception ex = new IllegalPropertyException(message);
		
		controller.handleException(ex, request, response);

		verify(spyOnLog).info(message, ex);
	}
	
	@Test
	public void handleConversionException_shouldLogConversionErrorAsInfo() throws Exception {
		String message = "conversion error";
		ConversionException ex = new ConversionException(message);
		SimpleObject responseSimpleObject = controller.conversionExceptionHandler(ex, request, response);
		assertThat(response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
		LinkedHashMap errors = (LinkedHashMap) responseSimpleObject.get("error");
		Assertions.assertEquals("[" + message + "]", errors.get("message"));
	}
	
	@Test
	public void httpMessageNotReadableExceptionHandler_shouldReturnBadRequestIfEmptyBody() throws Exception {
		controller.httpMessageNotReadableExceptionHandler(new HttpMessageNotReadableException("", (org.springframework.http.HttpInputMessage) null), request, response);
		assertThat(response.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
	}

	/**
	 * A controller that maps RuntimeException to a 400 itself, as Bahmni appointments'
	 * AppointmentUnavailabilityController does, and whose service call is denied. Not a
	 * {@code @Controller}, so the module context's component scan leaves it out; MockMvc's standalone
	 * setup registers it directly.
	 */
	@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/runtimeexceptionhandling")
	public static class RuntimeExceptionHandlingController extends BaseRestController {

		@RequestMapping
		@ResponseBody
		public SimpleObject deniedByTheService() {
			throw new AuthorizationDeniedException("Denied");
		}

		@ExceptionHandler(RuntimeException.class)
		@ResponseBody
		public SimpleObject runtimeExceptionHandler(RuntimeException ex, HttpServletResponse response) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return RestUtil.wrapErrorResponse(ex, "");
		}
	}
}
