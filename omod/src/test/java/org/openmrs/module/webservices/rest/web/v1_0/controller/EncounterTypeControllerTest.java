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

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests functionality of {@link EncounterTypeController}.
 */
public class EncounterTypeControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	private EncounterService service;
	//	
	//	private EncounterTypeController controller;
	//	
	//	private MockHttpServletRequest request;
	//	
	//	private HttpServletResponse response;
	//	
	//	@Before
	//	public void before() {
	//		this.service = Context.getEncounterService();
	//		this.controller = new EncounterTypeController();
	//		this.request = new MockHttpServletRequest();
	//		this.response = new MockHttpServletResponse();
	//	}
	//	
	//	@Test
	//	public void shouldGetAnEncounterTypeByUuid() throws Exception {
	//		Object result = controller.retrieve("61ae96f4-6afe-4351-b6f8-cd4fc383cce1", request);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals("61ae96f4-6afe-4351-b6f8-cd4fc383cce1", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertEquals("Scheduled", PropertyUtils.getProperty(result, "name"));
	//	}
	//	
	//	@Test
	//	public void shouldGetAnEncounterTypeByName() throws Exception {
	//		Object result = controller.retrieve("Scheduled", request);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals("61ae96f4-6afe-4351-b6f8-cd4fc383cce1", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertEquals("Scheduled", PropertyUtils.getProperty(result, "name"));
	//	}
	//	
	//	@Test
	//	public void shouldListAllUnRetiredEncounterTypes() throws Exception {
	//		SimpleObject result = controller.getAll(request, response);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals(2, Util.getResultsSize(result));
	//	}
	//	
	//	@Test
	//	public void shouldCreateAnEncounterType() throws Exception {
	//		int originalCount = service.getAllEncounterTypes().size();
	//		String json = "{ \"name\":\"test encounterType\", \"description\":\"description\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object newEncounterType = controller.create(post, request, response);
	//		Assert.assertNotNull(PropertyUtils.getProperty(newEncounterType, "uuid"));
	//		Assert.assertEquals(originalCount + 1, service.getAllEncounterTypes().size());
	//	}
	//	
	//	@Test
	//	public void shouldEditAnEncounterType() throws Exception {
	//		String json = "{ \"name\":\"new encounter type\", \"description\":\"new description\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		controller.update("61ae96f4-6afe-4351-b6f8-cd4fc383cce1", post, request, response);
	//		EncounterType updated = service.getEncounterTypeByUuid("61ae96f4-6afe-4351-b6f8-cd4fc383cce1");
	//		Assert.assertNotNull(updated);
	//		Assert.assertEquals("new encounter type", updated.getName());
	//		Assert.assertEquals("new description", updated.getDescription());
	//	}
	//	
	//	@Test
	//	public void shouldRetireAnEncounterType() throws Exception {
	//		String uuid = "61ae96f4-6afe-4351-b6f8-cd4fc383cce1";
	//		EncounterType encounterType = service.getEncounterTypeByUuid(uuid);
	//		Assert.assertFalse(encounterType.isRetired());
	//		controller.delete(uuid, "test reason", request, response);
	//		encounterType = service.getEncounterTypeByUuid(uuid);
	//		Assert.assertTrue(encounterType.isRetired());
	//		Assert.assertEquals("test reason", encounterType.getRetireReason());
	//	}
	//	
	//	@Test
	//	public void shouldPurgeAnEncounterType() throws Exception {
	//		//All the encounterTypes in the test dataset are already in use, so we need to
	//		//create one that we can purge for testing purposes
	//		EncounterType encounterType = new EncounterType();
	//		encounterType.setName("new test encounterType");
	//		encounterType.setDescription("new descriptionpe");
	//		service.saveEncounterType(encounterType);
	//		Assert.assertNotNull(encounterType.getUuid());//should have been inserted for the test to be valid
	//		int originalCount = service.getAllEncounterTypes().size();
	//		String uuid = encounterType.getUuid();
	//		controller.purge(uuid, request, response);
	//		Assert.assertNull(service.getEncounterTypeByUuid(uuid));
	//		Assert.assertEquals(originalCount - 1, service.getAllEncounterTypes().size());
	//	}
	//	
	//	@Test
	//	public void shouldSearchAndReturnAListOfEncounterTypesMatchingTheQueryString() throws Exception {
	//		List<Object> hits = (List<Object>) controller.search("Sch", request, response).get("results");
	//		Assert.assertEquals(1, hits.size());
	//		Assert.assertEquals("61ae96f4-6afe-4351-b6f8-cd4fc383cce1", PropertyUtils.getProperty(hits.get(0), "uuid"));
	//		
	//	}
	//	
	//	@Test
	//	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
	//		MockHttpServletRequest httpReq = new MockHttpServletRequest();
	//		httpReq.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//		Object result = controller.retrieve("61ae96f4-6afe-4351-b6f8-cd4fc383cce1", httpReq);
	//		Assert.assertNotNull(result);
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
