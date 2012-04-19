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
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests functionality of {@link PersonAttributeController}.
 */
public class PersonAttributeControllerTest extends BaseModuleWebContextSensitiveTest {
	
	String personUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
	
	String attributeUuid = "15d694af-43d6-443a-971c-ec210e3ec5c5";
	
	private PersonService service;
	
	private PersonAttributeController controller;
	
	private MockHttpServletRequest request;
	
	private HttpServletResponse response;
	
	@Before
	public void before() throws Exception {
		this.service = Context.getPersonService();
		this.controller = new PersonAttributeController();
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
	}
	
	@Test
	public void shouldGetAPersonAttribute() throws Exception {
		Object result = controller.retrieve(personUuid, attributeUuid, request);
		Assert.assertNotNull(result);
		Assert.assertEquals(attributeUuid, PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals("", PropertyUtils.getProperty(result, "value"));
		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	@Test
	public void shouldListAttributesForPerson() throws Exception {
		SimpleObject result = controller.getAll(personUuid, request, response);
		Assert.assertNotNull(result);
		Assert.assertEquals(3, ((List<Object>) PropertyUtils.getProperty(result, "results")).size());
	}
	
	@Test
	@Ignore("RESTWS-241: Define creatable/updatable properties on Person, PersonAddress, and PersonName resources")
	public void shouldAddAttributeToPerson() throws Exception {
		int before = service.getPersonByUuid(personUuid).getAttributes().size();
		String json = "{ \"attributeType\":\"b3b6d540-a32e-44c7-91b3-292d97667518\", \"value\":\"testing\"}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.create(personUuid, post, request, response);
		int after = service.getPersonByUuid(personUuid).getAttributes().size();
		Assert.assertEquals(before + 1, after);
	}
	
	@Test
	@Ignore("RESTWS-241: Define creatable/updatable properties on Person, PersonAddress, and PersonName resources")
	public void shouldEditAttribute() throws Exception {
		String json = "{ \"attributeType\":\"54fc8400-1683-4d71-a1ac-98d40836ff7c\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		
		PersonAttribute personAttribute = service.getPersonAttributeByUuid(attributeUuid);
		Assert.assertEquals("Race", personAttribute.getAttributeType().getName());
		
		controller.update(personUuid, attributeUuid, post, request, response);
		
		personAttribute = service.getPersonAttributeByUuid(attributeUuid);
		Assert.assertEquals("Birthplace", personAttribute.getAttributeType().getName());
	}
	
	@Test
	public void shouldVoidAttribute() throws Exception {
		PersonAttribute personAttribute = service.getPersonAttributeByUuid(attributeUuid);
		Assert.assertFalse(personAttribute.isVoided());
		controller.delete(personUuid, attributeUuid, "unit test", request, response);
		personAttribute = service.getPersonAttributeByUuid(attributeUuid);
		Assert.assertTrue(personAttribute.isVoided());
		Assert.assertEquals("unit test", personAttribute.getVoidReason());
	}
	
	@Test
	public void shouldPurgeAttribute() throws Exception {
		// I'm using sql queries and a flush-session because if I try to test this the natural way, hibernate
		// complains that the attribute will be re-created since the person is in the session.
		Number before = (Number) Context.getAdministrationService().executeSQL(
		    "select count(*) from person_attribute where person_id = 2", true).get(0).get(0);
		
		controller.purge(personUuid, attributeUuid, request, response);
		Context.flushSession();
		Number after = (Number) Context.getAdministrationService().executeSQL(
		    "select count(*) from person_attribute where person_id = 2", true).get(0).get(0);
		Assert.assertEquals(before.intValue() - 1, after.intValue());
		Assert.assertNull(service.getPersonAttributeByUuid(attributeUuid));
	}
}
