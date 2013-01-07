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

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.activelist.Problem;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.resource.ProblemResource;
import org.openmrs.module.webservices.rest.web.v1_0.resource.ResourceTestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.WebRequest;

public class ProblemControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	private static final String ACTIVE_LIST_INITIAL_XML = "customActiveListTest.xml";
	//	
	//	private MockHttpServletRequest emptyRequest() {
	//		return new MockHttpServletRequest();
	//	}
	//	
	//	@Before
	//	public void init() throws Exception {
	//		executeDataSet(ACTIVE_LIST_INITIAL_XML);
	//	}
	//	
	//	/**
	//	 * @see ProblemController#getProblem(String,WebRequest)
	//	 * @verifies get a default representation of a problem
	//	 */
	//	@Test
	//	public void getProblem_shouldGetADefaultRepresentationOfAProblem() throws Exception {
	//		Object result = new ProblemController().retrieve(ResourceTestConstants.PROBLEM_UUID, emptyRequest());
	//		Assert.assertNotNull(result);
	//		Util.log("Problem fetched (default)", result);
	//		Assert.assertEquals(ResourceTestConstants.PROBLEM_UUID, PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "problem"));
	//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	/**
	//	 * @see ProblemController#getProblem(String,WebRequest)
	//	 * @verifies get a full representation of a problem
	//	 */
	//	@Test
	//	public void getProblem_shouldGetAFullRepresentationOfAProblem() throws Exception {
	//		MockHttpServletRequest req = new MockHttpServletRequest();
	//		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//		Object result = new ProblemController().retrieve(ResourceTestConstants.PROBLEM_UUID, req);
	//		Assert.assertNotNull(result);
	//		Util.log("Problem fetched (default)", result);
	//		Assert.assertEquals(ResourceTestConstants.PROBLEM_UUID, PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "problem"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	/**
	//	 * @see ProblemController#voidProblem(String,String,WebRequest,HttpServletResponse)
	//	 * @verifies void a problem
	//	 */
	//	@Test
	//	public void voidProblem_shouldVoidAProblem() throws Exception {
	//		Problem problem = Context.getPatientService().getProblem(2);
	//		Assert.assertFalse(problem.isVoided());
	//		new ProblemController().delete(ResourceTestConstants.PROBLEM_UUID, "unit test", emptyRequest(),
	//		    new MockHttpServletResponse());
	//		problem = Context.getPatientService().getProblem(2);
	//		Assert.assertTrue(problem.isVoided());
	//		Assert.assertEquals("unit test", problem.getVoidReason());
	//	}
	//	
	//	/**
	//	 * @see ProblemResource#getProblemByPatient(String,
	//	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	//	 * @throws Exception
	//	 */
	//	@SuppressWarnings("unchecked")
	//	@Test
	//	public void searchByPatient_shouldGetProblemForAPatient() throws Exception {
	//		SimpleObject search = new ProblemController().searchByPatient("da7f524f-27ce-4bb2-86d6-6d1d05312bd5",
	//		    emptyRequest(), new MockHttpServletResponse());
	//		List<Object> results = (List<Object>) search.get("results");
	//		Assert.assertEquals(1, results.size());
	//		Assert.assertEquals(ResourceTestConstants.PROBLEM_UUID, PropertyUtils.getProperty(results.get(0), "uuid"));
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
