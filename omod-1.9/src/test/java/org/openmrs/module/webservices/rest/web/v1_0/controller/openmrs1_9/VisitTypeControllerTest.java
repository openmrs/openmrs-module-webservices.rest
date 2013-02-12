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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

/**
 * Contains tests for the {@link VisitTypeController}
 */
public class VisitTypeControllerTest extends BaseModuleWebContextSensitiveTest {
	
	@Test
	public void fakeTest() {
		
	}
	
//	private VisitService service;
//	
//	private VisitTypeController controller;
//	
//	private MockHttpServletRequest request;
//	
//	private HttpServletResponse response;
//	
//	@Before
//	public void before() {
//		this.service = Context.getVisitService();
//		this.controller = new VisitTypeController();
//		this.request = new MockHttpServletRequest();
//		this.response = new MockHttpServletResponse();
//	}
//	
//	@Test
//	public void shouldGetAVisitTypeByUuid() throws Exception {
//		Object result = controller.retrieve(Rest19ExtTestConstants.VISIT_TYPE_UUID, request);
//		Assert.assertNotNull(result);
//		Assert.assertEquals(Rest19ExtTestConstants.VISIT_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertEquals("Return TB Clinic Visit", PropertyUtils.getProperty(result, "name"));
//	}
//	
//	@Test
//	public void shouldGetAVisitTypeByName() throws Exception {
//		Object result = controller.retrieve("Return TB Clinic Visit", request);
//		Assert.assertNotNull(result);
//		Assert.assertEquals(Rest19ExtTestConstants.VISIT_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertEquals("Return TB Clinic Visit", PropertyUtils.getProperty(result, "name"));
//	}
//	
//	@Test
//	public void shouldListAllUnRetiredVisitTypes() throws Exception {
//		SimpleObject result = controller.getAll(request, response);
//		Assert.assertNotNull(result);
//		Assert.assertEquals(2, Util.getResultsSize(result));
//	}
//	
//	@Test
//	public void shouldCreateAVisitType() throws Exception {
//		int originalCount = service.getAllVisitTypes().size();
//		String json = "{ \"name\":\"test visitType\", \"description\":\"description\" }";
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		Object newVisitType = controller.create(post, request, response);
//		Assert.assertNotNull(PropertyUtils.getProperty(newVisitType, "uuid"));
//		Assert.assertEquals(originalCount + 1, service.getAllVisitTypes().size());
//	}
//	
//	@Test
//	public void shouldEditAVisitType() throws Exception {
//		String json = "{ \"name\":\"new visit type\", \"description\":\"new description\" }";
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		controller.update(Rest19ExtTestConstants.VISIT_TYPE_UUID, post, request, response);
//		VisitType updated = service.getVisitTypeByUuid(Rest19ExtTestConstants.VISIT_TYPE_UUID);
//		Assert.assertNotNull(updated);
//		Assert.assertEquals("new visit type", updated.getName());
//		Assert.assertEquals("new description", updated.getDescription());
//	}
//	
//	@Test
//	public void shouldRetireAVisitType() throws Exception {
//		VisitType visitType = service.getVisitTypeByUuid(Rest19ExtTestConstants.VISIT_TYPE_UUID);
//		Assert.assertFalse(visitType.isRetired());
//		controller.delete(Rest19ExtTestConstants.VISIT_TYPE_UUID, "test reason", request, response);
//		visitType = service.getVisitTypeByUuid(Rest19ExtTestConstants.VISIT_TYPE_UUID);
//		Assert.assertTrue(visitType.isRetired());
//		Assert.assertEquals("test reason", visitType.getRetireReason());
//	}
//	
//	@Test
//	public void shouldPurgeAVisitType() throws Exception {
//		String uuid = "759799ab-c9a5-435e-b671-77773ada74e6";
//		Assert.assertNotNull(service.getVisitTypeByUuid(uuid));
//		int originalCount = service.getAllVisitTypes().size();
//		controller.purge(uuid, request, response);
//		Assert.assertNull(service.getVisitTypeByUuid(uuid));
//		Assert.assertEquals(originalCount - 1, service.getAllVisitTypes().size());
//	}
//	
//	@SuppressWarnings("unchecked")
//	@Test
//	public void shouldSearchAndReturnAListOfVisitTypesMatchingTheQueryString() throws Exception {
//		List<Object> hits = (List<Object>) controller.search("Ret", request, response).get("results");
//		Assert.assertEquals(1, hits.size());
//		Assert.assertEquals(Rest19ExtTestConstants.VISIT_TYPE_UUID, PropertyUtils.getProperty(hits.get(0), "uuid"));
//		
//	}
//	
//	@SuppressWarnings("unchecked")
//	@Test
//	public void shouldSearchAndReturnAListOfVisitTypesMatchingTheQueryStringExcludingRetiredOnes() throws Exception {
//		final String searchString = "Hos";
//		//sanity check
//		Assert.assertEquals(1, Context.getVisitService().getVisitTypes(searchString).size());
//		List<Object> hits = (List<Object>) controller.search(searchString, request, response).get("results");
//		Assert.assertEquals(0, hits.size());
//		
//	}
//	
//	@Test
//	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
//		request.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
//		Object result = controller.retrieve(Rest19ExtTestConstants.VISIT_TYPE_UUID, request);
//		Assert.assertNotNull(result);
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
//	}
}
