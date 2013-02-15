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

import java.util.List;
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

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ResourceTestConstants;

/**
 * Tests functionality of {@link ConceptClassController}.
 */
public class ConceptClassControllerTest extends BaseCrudControllerTest {
	
	private ConceptService service;
	
	//private ConceptClassController controller;
	private MockHttpServletRequest request;
	
	//	private HttpServletResponse response;
	
	private static final String ACTIVE_LIST_INITIAL_XML = "customActiveListTest.xml";
	
	@Before
	public void init() throws Exception {
		executeDataSet(ACTIVE_LIST_INITIAL_XML);
	}
	
	@Test
	public void getConceptClass_shouldGetADefaultRepresentationOfAConceptClass() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Util.log("ConceptClass fetched (default)", result);
		Assert.assertEquals(getUuid(), result.get("uuid"));
	}
	
	@Test
	public void getConceptClass_shouldGetAConceptClassByUuid() throws Exception {
		String conceptClassUuid = "ecdee8a7-d741-4fe7-8e01-f79cacbe97bc";
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + conceptClassUuid);
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		Util.log("ConceptClass fetched (default)", result);
		Assert.assertEquals(conceptClassUuid, result.get("uuid"));
		
	}
	
	public void getConceptClass_shouldGetAConceptClassByName() throws Exception {
		String conceptClassName = "Drug";
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + conceptClassName);
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		Util.log("ConceptClassName fetched (default)", result);
		Assert.assertEquals(conceptClassName, result.get("name"));
	}
	
	@Test
	public void getConceptClass_shouldListAllConceptClasses() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		List<Object> results = (List<Object>) result.get("results");
		Assert.assertEquals(getAllCount(), results.size());
		Util.log("Found " + results.size() + " conceptClasse(s)", results);
	}
	
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
	@Test
	public void shouldRetireAConceptClass() throws Exception {
		String uuid = "97097dd9-b092-4b68-a2dc-e5e5be961d42";
		
		Assert.assertEquals(false, service.getConceptClassByUuid(uuid).isRetired());
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + uuid);
		req.addParameter("!purge", "");
		final String reason = "none";
		req.addParameter("reason", reason);
		handle(req);
		Assert.assertEquals(true, service.getConceptClassByUuid(uuid).isRetired());
		Assert.assertEquals(reason, service.getConceptClassByUuid(uuid).getRetireReason());
	}
	
	/*
	public void shouldRetireAConceptClass() throws Exception {
		String uuid = "97097dd9-b092-4b68-a2dc-e5e5be961d42";
		
		Assert.assertNotNull(service.getConceptClassByUuid(uuid));
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + uuid);
		req.addParameter("purge", "");
		handle(req);
		Assert.assertNull(service.getConceptClassByUuid(uuid));
	}
	*/
	public void shouldPurgeAConceptClass() throws Exception {
		String uuid = "77177ce7-1410-40ee-bbad-ff6905ee3095";
		
		Assert.assertNotNull(service.getConceptClassByUuid(uuid));
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + uuid);
		req.addParameter("purge", "");
		handle(req);
		Assert.assertNull(service.getConceptClassByUuid(uuid));
	}
	
	//	@Test
	//	public void shouldNotIncludeTheAuditInfoForTheDefaultRepresentation() throws Exception {
	//		Object result = controller.retrieve("97097dd9-b092-4b68-a2dc-e5e5be961d42", request);
	//		Assert.assertNotNull(result);
	//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//@Test
	//public void shouldIncludeTheAuditInfoForTheFullRepresentation() throws Exception {
	//	MockHttpServletRequest httpReq = new MockHttpServletRequest();
	//	httpReq.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//	Object result = controller.retrieve("97097dd9-b092-4b68-a2dc-e5e5be961d42", httpReq);
	//	Assert.assertNotNull(result);
	//	Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	//}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "conceptclass";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return ResourceTestConstants.CONCEPT_CLASS_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return Context.getConceptService().getAllConceptClasses().size();
	}
	
}
