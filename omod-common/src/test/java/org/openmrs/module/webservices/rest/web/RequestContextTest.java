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
package org.openmrs.module.webservices.rest.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.openmrs.api.APIException;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;

public class RequestContextTest extends BaseModuleWebContextSensitiveTest {
	
	/**
	 * @see RequestContext#setLimit(Integer)
	 * @verifies not accept a value less than one
	 */
	@Test(expected = APIException.class)
	public void setLimit_shouldNotAcceptAValueLessThanOne() throws Exception {
		new RequestContext().setLimit(0);
	}
	
	/**
	 * @see RequestContext#setLimit(Integer)
	 * @verifies not accept a null value
	 */
	@Test(expected = APIException.class)
	public void setLimit_shouldNotAcceptANullValue() throws Exception {
		new RequestContext().setLimit(null);
	}
	
	/**
	 * @see RequestContext#getParameter(String)
	 * @verifies return null if request is null
	 */
	@Test
	public void getParameter_shouldReturnNullIfRequestIsNull() throws Exception {
		
		RequestContext requestContext = new RequestContext();
		
		assertNull(requestContext.getParameter("UNKOWN"));
	}
	
	/**
	 * @see RequestContext#getParameter(String)
	 * @verifies return null if the wanted request parameter is not present in the request
	 */
	@Test
	public void getParameter_shouldReturnNullIfTheWantedRequestParameterIsNotPresentInTheRequest() throws Exception {
		
		RequestContext requestContext = new RequestContext();
		MockHttpServletRequest request = new MockHttpServletRequest();
		requestContext.setRequest(request);
		
		assertNull(requestContext.getParameter("UNKOWN"));
	}
	
	/**
	 * @see RequestContext#getParameter(String)
	 * @verifies return the request parameter of given name if present in the request
	 */
	@Test
	public void getParameter_shouldReturnTheRequestParameterOfGivenNameIfPresentInTheRequest() throws Exception {
		
		RequestContext requestContext = new RequestContext();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("includeAll", "true");
		requestContext.setRequest(request);
		
		assertThat(requestContext.getParameter("includeAll"), is("true"));
	}
}
