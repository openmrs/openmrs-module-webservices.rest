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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.ConceptClass;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests functionality of {@link ConceptClassController}.
 */
public class ConceptClassControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	private ConceptService service;
	//	
	//	private ConceptClassController controller;
	//	
	//	private MockHttpServletRequest request;
	//	
	//	private HttpServletResponse response;
	//	
	//	@Before
	//	public void before() {
	//		this.service = Context.getConceptService();
	//		this.controller = new ConceptClassController();
	//		this.request = new MockHttpServletRequest();
	//		this.response = new MockHttpServletResponse();
	//	}
	//	
	//	@Test
	//	public void shouldGetAConceptClassByUuid() throws Exception {
	//		Object result = controller.retrieve("97097dd9-b092-4b68-a2dc-e5e5be961d42", request);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals("97097dd9-b092-4b68-a2dc-e5e5be961d42", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertEquals("Test", PropertyUtils.getProperty(result, "name"));
	//	}
	//	
	//	@Test
	//	public void shouldGetAConceptClassByName() throws Exception {
	//		Object result = controller.retrieve("Test", request);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals("97097dd9-b092-4b68-a2dc-e5e5be961d42", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertEquals("Test", PropertyUtils.getProperty(result, "name"));
	//	}
	//	
	//	@Test
	//	public void shouldListAllConceptClasss() throws Exception {
	//		SimpleObject result = controller.getAll(request, response);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals(18, Util.getResultsSize(result));
	//	}
	//	
	//	@Test
	//	@Ignore("RESTWS-228: Define creatable/updatable properties on ConceptClass resource")
	//	public void shouldCreateAConceptClass() throws Exception {
	//		int originalCount = service.getAllConceptClasses().size();
	//		String json = "{ \"name\":\"test conceptClass\", \"description\":\"test descr\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object newConceptClass = controller.create(post, request, response);
	//		Assert.assertNotNull(PropertyUtils.getProperty(newConceptClass, "uuid"));
	//		Assert.assertEquals(originalCount + 1, service.getAllConceptClasses().size());
	//	}
	//	
	//	@Test
	//	@Ignore("RESTWS-228: Define creatable/updatable properties on ConceptClass resource")
	//	public void shouldEditAConceptClass() throws Exception {
	//		String json = "{ \"name\":\"new class name\", \"description\":\"new class description\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		controller.update("97097dd9-b092-4b68-a2dc-e5e5be961d42", post, request, response);
	//		ConceptClass updated = service.getConceptClassByUuid("97097dd9-b092-4b68-a2dc-e5e5be961d42");
	//		Assert.assertNotNull(updated);
	//		Assert.assertEquals("new class name", updated.getName());
	//		Assert.assertEquals("new class description", updated.getDescription());
	//	}
	//	
	//	@Test
	//	public void shouldRetireAConceptClass() throws Exception {
	//		String uuid = "97097dd9-b092-4b68-a2dc-e5e5be961d42";
	//		ConceptClass conceptClass = service.getConceptClassByUuid(uuid);
	//		Assert.assertFalse(conceptClass.isRetired());
	//		controller.delete(uuid, "test reason", request, response);
	//		conceptClass = service.getConceptClassByUuid(uuid);
	//		Assert.assertTrue(conceptClass.isRetired());
	//		Assert.assertEquals("test reason", conceptClass.getRetireReason());
	//	}
	//	
	//	@Test
	//	public void shouldPurgeAConceptClass() throws Exception {
	//		int originalCount = service.getAllConceptClasses().size();
	//		String uuid = "77177ce7-1410-40ee-bbad-ff6905ee3095";
	//		controller.purge(uuid, request, response);
	//		Assert.assertNull(service.getConceptClassByUuid(uuid));
	//		Assert.assertEquals(originalCount - 1, service.getAllConceptClasses().size());
	//	}
	//	
	//	@Test
	//	public void shouldNotIncludeTheAuditInfoForTheDefaultRepresentation() throws Exception {
	//		Object result = controller.retrieve("97097dd9-b092-4b68-a2dc-e5e5be961d42", request);
	//		Assert.assertNotNull(result);
	//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	@Test
	//	public void shouldIncludeTheAuditInfoForTheFullRepresentation() throws Exception {
	//		MockHttpServletRequest httpReq = new MockHttpServletRequest();
	//		httpReq.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//		Object result = controller.retrieve("97097dd9-b092-4b68-a2dc-e5e5be961d42", httpReq);
	//		Assert.assertNotNull(result);
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
