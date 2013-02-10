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

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PersonName;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests functionality of {@link PersonNameController}.
 */
public class PersonNameControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	
	//	String personUuid = "ba1b19c2-3ed6-4f63-b8c0-f762dc8d7562";
	//	
	//	String nameUuid = "38a686df-d459-484c-9e7c-3f43a9bced58";
	//	
	//	private PersonService service;
	//	
	//	private PersonNameController controller;
	//	
	//	private MockHttpServletRequest request;
	//	
	//	private HttpServletResponse response;
	//	
	//	@Before
	//	public void before() throws Exception {
	//		this.service = Context.getPersonService();
	//		this.controller = new PersonNameController();
	//		this.request = new MockHttpServletRequest();
	//		this.response = new MockHttpServletResponse();
	//	}
	//	
	//	@Test
	//	public void shouldGetAPersonName() throws Exception {
	//		Object result = controller.retrieve(personUuid, nameUuid, request);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals("Super", PropertyUtils.getProperty(result, "givenName"));
	//		Assert.assertEquals("User", PropertyUtils.getProperty(result, "familyName"));
	//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "uuid"));
	//	}
	//	
	//	@Test
	//	public void shouldListNamesForPerson() throws Exception {
	//		SimpleObject result = controller.getAll(personUuid, request, response);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals(1, Util.getResultsSize(result));
	//	}
	//	
	//	@Test
	//	public void shouldAddNameToPerson() throws Exception {
	//		int before = service.getPersonByUuid(personUuid).getNames().size();
	//		String json = "{ \"givenName\":\"name1\", \"middleName\":\"name2\", \"familyName\":\"name3\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		controller.create(personUuid, post, request, response);
	//		int after = service.getPersonByUuid(personUuid).getNames().size();
	//		Assert.assertEquals(before + 1, after);
	//	}
	//	
	//	@Test
	//	public void shouldEditName() throws Exception {
	//		String json = "{ \"familyName\":\"newName\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		PersonName personName = service.getPersonNameByUuid(nameUuid);
	//		Assert.assertEquals("User", personName.getFamilyName());
	//		controller.update(personUuid, nameUuid, post, request, response);
	//		personName = service.getPersonNameByUuid(nameUuid);
	//		Assert.assertNotNull(personName);
	//		Assert.assertEquals("newName", personName.getFamilyName());
	//	}
	//	
	//	@Test
	//	public void shouldVoidName() throws Exception {
	//		PersonName pname = service.getPersonNameByUuid(nameUuid);
	//		Assert.assertFalse(pname.isVoided());
	//		controller.delete(personUuid, nameUuid, "unit test", request, response);
	//		pname = service.getPersonNameByUuid(nameUuid);
	//		Assert.assertTrue(pname.isVoided());
	//		Assert.assertEquals("unit test", pname.getVoidReason());
	//	}
	//	
	//	@Test
	//	public void shouldPurgeName() throws Exception {
	//		// I'm using sql queries and a flush-session because if I try to test this the natural way, hibernate
	//		// complains that the name will be re-created since the person is in the session.
	//		Number before = (Number) Context.getAdministrationService().executeSQL(
	//		    "select count(*) from person_name where person_id = 1", true).get(0).get(0);
	//		
	//		controller.purge(personUuid, nameUuid, request, response);
	//		Context.flushSession();
	//		Number after = (Number) Context.getAdministrationService().executeSQL(
	//		    "select count(*) from person_name where person_id = 1", true).get(0).get(0);
	//		Assert.assertEquals(before.intValue() - 1, after.intValue());
	//		Assert.assertNull(service.getPersonNameByUuid(nameUuid));
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
