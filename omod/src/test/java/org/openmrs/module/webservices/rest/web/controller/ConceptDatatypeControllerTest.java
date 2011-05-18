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
package org.openmrs.module.webservices.rest.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptDatatype;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Tests functionality of {@link ConceptDatatypeController}.
 */
public class ConceptDatatypeControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private ConceptService service;
	
	private ConceptDatatypeController controller;
	
	private WebRequest request;
	
	private HttpServletResponse response;
	
	@Before
	public void before() {
		this.service = Context.getConceptService();
		this.controller = new ConceptDatatypeController();
		this.request = new ServletWebRequest(new MockHttpServletRequest());
		this.response = new MockHttpServletResponse();
	}
	
	@Test
	public void shouldGetAConceptDatatypeByUuid() throws Exception {
		Object result = controller.retrieve("8d4a4488-c2cc-11de-8d13-0010c6dffd0f", request);
		Assert.assertNotNull(result);
		Assert.assertEquals("8d4a4488-c2cc-11de-8d13-0010c6dffd0f", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals("Numeric", PropertyUtils.getProperty(result, "name"));
	}
	
	@Test
	public void shouldGetAConceptDatatypeByName() throws Exception {
		Object result = controller.retrieve("Coded", request);
		Assert.assertNotNull(result);
		Assert.assertEquals("8d4a48b6-c2cc-11de-8d13-0010c6dffd0f", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals("Coded", PropertyUtils.getProperty(result, "name"));
	}
	
	@Test
	public void shouldListAllConceptDatatypes() throws Exception {
		List<Object> result = controller.getAll(request, response);
		Assert.assertNotNull(result);
		Assert.assertEquals(12, result.size());
	}
	
	@Test
	public void shouldCreateAConceptDatatype() throws Exception {
		int originalCount = service.getAllConceptDatatypes().size();
		String json = "{ \"name\":\"test conceptDatatype\", \"description\":\"test descr\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object newConceptDatatype = controller.create(post, request, response);
		Assert.assertNotNull(PropertyUtils.getProperty(newConceptDatatype, "uuid"));
		Assert.assertEquals(originalCount + 1, service.getAllConceptDatatypes().size());
	}
	
	@Test
	public void shouldEditAConceptDatatype() throws Exception {
		String json = "{ \"name\":\"new coded name\", \"description\":\"new description\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.update("8d4a48b6-c2cc-11de-8d13-0010c6dffd0f", post, request, response);
		ConceptDatatype updated = service.getConceptDatatypeByUuid("8d4a48b6-c2cc-11de-8d13-0010c6dffd0f");
		Assert.assertNotNull(updated);
		Assert.assertEquals("new coded name", updated.getName());
		Assert.assertEquals("new description", updated.getDescription());
	}
	
	@Test
	public void shouldRetireAConceptDatatype() throws Exception {
		String uuid = "8d4a606c-c2cc-11de-8d13-0010c6dffd0f";
		ConceptDatatype conceptDatatype = service.getConceptDatatypeByUuid(uuid);
		Assert.assertFalse(conceptDatatype.isRetired());
		controller.delete(uuid, "test reason", request, response);
		conceptDatatype = service.getConceptDatatypeByUuid(uuid);
		Assert.assertTrue(conceptDatatype.isRetired());
		Assert.assertEquals("test reason", conceptDatatype.getRetireReason());
	}
	
	@Test
	public void shouldPurgeAConceptDatatype() throws Exception {
		int originalCount = service.getAllConceptDatatypes().size();
		String uuid = "8d4a606c-c2cc-11de-8d13-0010c6dffd0f";
		controller.purge(uuid, request, response);
		Assert.assertNull(service.getConceptDatatypeByUuid(uuid));
		Assert.assertEquals(originalCount - 1, service.getAllConceptDatatypes().size());
	}
	
	@Test
	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
		MockHttpServletRequest httpReq = new MockHttpServletRequest();
		httpReq.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		Object result = controller.retrieve("8d4a606c-c2cc-11de-8d13-0010c6dffd0f", new ServletWebRequest(httpReq));
		Assert.assertNotNull(result);
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
}
