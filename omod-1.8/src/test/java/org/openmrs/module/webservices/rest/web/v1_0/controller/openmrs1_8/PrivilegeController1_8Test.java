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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Privilege;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class PrivilegeController1_8Test extends MainResourceControllerTest {
	
	private static final String XML_FILENAME = "org/openmrs/api/include/UserServiceTest.xml";
	
	private UserService service;
	
	@Override
	public String getURI() {
		return "privilege";
	}
	
	@Override
	public String getUuid() {
		return RestTestConstants1_8.PRIVILEGE_UUID;
	}
	
	@Override
	public long getAllCount() {
		return service.getAllPrivileges().size();
	}
	
	@Before
	public void init() throws Exception {
		executeDataSet(XML_FILENAME);
		service = Context.getUserService();
	}
	
	@Test
	public void shouldGetAPrivilegeByUuid() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		
		Privilege privilege = service.getPrivilegeByUuid(getUuid());
		assertEquals(privilege.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		assertEquals(privilege.getPrivilege(), PropertyUtils.getProperty(result, "name"));
		assertEquals(privilege.getDescription(), PropertyUtils.getProperty(result, "description"));
	}
	
	@Test
	public void shouldGetAPrivilegeByName() throws Exception {
		final String name = "Some Privilege";
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + name);
		SimpleObject result = deserialize(handle(req));
		
		Privilege privilege = service.getPrivilege(name);
		assertEquals(privilege.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		assertEquals(privilege.getPrivilege(), PropertyUtils.getProperty(result, "name"));
	}
	
	@Test
	public void shouldListAllPrivileges() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		
		assertNotNull(result);
		assertEquals(getAllCount(), Util.getResultsSize(result));
	}
	
	@Test
	public void shouldCreateAPrivilege() throws Exception {
		long originalCount = getAllCount();
		
		SimpleObject privilege = new SimpleObject();
		privilege.add("name", "test name");
		privilege.add("description", "test description");
		
		String json = new ObjectMapper().writeValueAsString(privilege);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		SimpleObject newPrivilege = deserialize(handle(req));
		
		assertNotNull(PropertyUtils.getProperty(newPrivilege, "uuid"));
		assertEquals(originalCount + 1, getAllCount());
	}
	
	@Test
	public void shouldEditingAPrivilege() throws Exception {
		final String newDescription = "updated descr";
		SimpleObject privilege = new SimpleObject();
		assertEquals(false, newDescription.equals(service.getPrivilegeByUuid(getUuid()).getName()));
		privilege.add("description", newDescription);
		
		String json = new ObjectMapper().writeValueAsString(privilege);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/" + getUuid());
		req.setContent(json.getBytes());
		handle(req);
		assertEquals(newDescription, service.getPrivilegeByUuid(getUuid()).getDescription());
	}
	
	@Test
	public void shouldPurgeAPrivilege() throws Exception {
		assertNotNull(service.getPrivilegeByUuid(getUuid()));
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		req.addParameter("purge", "");
		handle(req);
		assertNull(service.getPrivilegeByUuid(getUuid()));
	}
	
	@Test
	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));
		
		assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
}
