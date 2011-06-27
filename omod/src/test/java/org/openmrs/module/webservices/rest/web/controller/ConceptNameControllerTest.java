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

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class ConceptNameControllerTest extends BaseModuleWebContextSensitiveTest {
	
	String conceptUuid = "0cbe2ed3-cd5f-4f46-9459-26127c9265ab";
	
	String nameUuid = "b8159118-c97b-4d5a-a63e-d4aa4be0c4d3";
	
	private ConceptService service;
	
	private ConceptNameController controller;
	
	private MockHttpServletRequest request;
	
	private HttpServletResponse response;
	
	@Before
	public void before() throws Exception {
		this.service = Context.getConceptService();
		this.controller = new ConceptNameController();
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
	}
	
	@Test
	public void shouldGetAConceptName() throws Exception {
		Object result = controller.retrieve(conceptUuid, nameUuid, request);
		Assert.assertNotNull(result);
		Assert.assertEquals("COUGH SYRUP", PropertyUtils.getProperty(result, "name"));
		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	@Test
	public void shouldAddNameToConcept() throws Exception {
		int before = service.getConceptByUuid(conceptUuid).getNames().size();
		String json = "{ \"name\":\"COUGH SYRUP II\", \"locale\":\"en\", \"conceptNameType\":\""
		        + ConceptNameType.FULLY_SPECIFIED + "\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.create(conceptUuid, post, request, response);
		int after = service.getConceptByUuid(conceptUuid).getNames().size();
		Assert.assertEquals(before + 1, after);
	}
	
}
