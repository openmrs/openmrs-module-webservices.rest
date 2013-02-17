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
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Privilege;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PrivilegeResource;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class PrivilegeControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	private String uuid;
	//	
	//	/**
	//	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#newObject()
	//	 */
	//	@Before
	//	public void createPrivilege() {
	//		Privilege privilege = new Privilege("PrivilegeResourceTest Privilege", "This privilege is only for testing.");
	//		privilege.setUuid(UUID.randomUUID().toString()); //Uuid isn't assigned during creation until 1.8.1. 
	//		uuid = privilege.getUuid();
	//		Context.getUserService().savePrivilege(privilege);
	//	}
	//	
	//	/**
	//	 * @see PrivilegeController#create(SimpleObject, javax.servlet.http.HttpServletRequest, HttpServletResponse)
	//	 * @verifies create a new Privilege
	//	 */
	//	@Test
	//	public void createPrivilege_shouldCreateANewPrivilege() throws Exception {
	//		int before = Context.getUserService().getAllPrivileges().size();
	//		String json = "{\"name\":\"test\",\"description\":\"This is a test privilege for PrivilegeControllerTest.\"}}";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object newPrivilege = new PrivilegeController().create(post, new MockHttpServletRequest(),
	//		    new MockHttpServletResponse());
	//		Assert.assertEquals(before + 1, Context.getUserService().getAllPrivileges().size());
	//	}
	//	
	//	/**
	//	 * @see PrivilegeController#retrieve(String, javax.servlet.http.HttpServletRequest)
	//	 * @verifies get a default representation of a Privilege
	//	 */
	//	@Test
	//	public void getPrivilege_shouldGetADefaultRepresentationOfAPrivilege() throws Exception {
	//		Object result = new PrivilegeController().retrieve(uuid, new MockHttpServletRequest());
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals(uuid, PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertEquals("PrivilegeResourceTest Privilege", PropertyUtils.getProperty(result, "name"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "description"));
	//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	/**
	//	 * @see PrivilegeController#retrieve(String, javax.servlet.http.HttpServletRequest)
	//	 * @verifies get a full representation of a Privilege
	//	 */
	//	@Test
	//	public void getPrivilege_shouldGetAFullRepresentationOfAPrivilege() throws Exception {
	//		MockHttpServletRequest req = new MockHttpServletRequest();
	//		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//		Object result = new PrivilegeController().retrieve(uuid, req);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals(uuid, PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertEquals("PrivilegeResourceTest Privilege", PropertyUtils.getProperty(result, "name"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "description"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	/**
	//	 * @see PrivilegeController#update(String, SimpleObject, javax.servlet.http.HttpServletRequest, HttpServletResponse)
	//	 * @verifies change a property on a Privilege
	//	 */
	//	@Test
	//	public void updatePrivilege_shouldChangeAPropertyOnAPrivilege() throws Exception {
	//		Privilege privilege = new PrivilegeResource().getByUniqueId(uuid);
	//		Assert.assertFalse("Updated Description".equals(privilege.getDescription()));
	//		SimpleObject post = new ObjectMapper().readValue("{\"description\":\"Updated Description\"}", SimpleObject.class);
	//		Object editedPrivilege = new PrivilegeController().update(uuid, post, new MockHttpServletRequest(),
	//		    new MockHttpServletResponse());
	//		privilege = new PrivilegeResource().getByUniqueId(uuid);
	//		Assert.assertEquals("Updated Description", privilege.getDescription());
	//	}
	//	
	//	/**
	//	 * @see PrivilegeController#delete(String, String, javax.servlet.http.HttpServletRequest, HttpServletResponse)
	//	 * @verifies void a Privilege
	//	 */
	//	@Test
	//	public void retirePrivilege_shouldRetireAPrivilege() throws Exception {
	//		Privilege privilege = new PrivilegeResource().getByUniqueId(uuid);
	//		Assert.assertFalse(privilege.isRetired());
	//		new PrivilegeController().delete(uuid, "unit test", new MockHttpServletRequest(), new MockHttpServletResponse());
	//		privilege = new PrivilegeResource().getByUniqueId(uuid);
	//		Assert.assertTrue(privilege.isRetired());
	//		Assert.assertEquals("unit test", privilege.getRetireReason());
	//	}
	//	
	//	/**
	//	 * @see PrivilegeController#search(String, javax.servlet.http.HttpServletRequest, HttpServletResponse)
	//	 * @verifies return no results if there are no matching Privileges
	//	 */
	//	@Test
	//	public void findPrivileges_shouldReturnNoResultsIfThereAreNoMatchingPrivileges() throws Exception {
	//		List<Object> results = (List<Object>) new PrivilegeController().search("zzzznothing", new MockHttpServletRequest(),
	//		    new MockHttpServletResponse()).get("results");
	//		Assert.assertEquals(0, results.size());
	//	}
	//	
	//	/**
	//	 * @see PrivilegeController#search(String, javax.servlet.http.HttpServletRequest, HttpServletResponse)
	//	 * @verifies find matching Privileges
	//	 */
	//	@Test
	//	@Ignore("Privileges do not support searching yet.")
	//	public void findPrivileges_shouldFindMatchingPrivileges() throws Exception {
	//		List<Object> results = (List<Object>) new UserController().search("PrivilegeResourceTest Privilege",
	//		    new MockHttpServletRequest(), new MockHttpServletResponse()).get("results");
	//		Assert.assertEquals(1, results.size());
	//		Object result = results.get(0);
	//		Assert.assertEquals(uuid, PropertyUtils.getProperty(result, "uuid"));
	//	}
	//	
	//	/**
	//	 * @see PrivilegeController#getAll(javax.servlet.http.HttpServletRequest, HttpServletResponse)
	//	 * @verifies get all Privileges
	//	 */
	//	@Test
	//	public void shouldListAllPrivileges() throws Exception {
	//		int totalCount = Context.getUserService().getAllPrivileges().size();
	//		
	//		SimpleObject result = new PrivilegeController().getAll(new MockHttpServletRequest(), new MockHttpServletResponse());
	//		
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals(totalCount, Util.getResultsSize(result));
	//	}
	//	
	
	@Test
	public void fakeTest() {
		
	}
}
