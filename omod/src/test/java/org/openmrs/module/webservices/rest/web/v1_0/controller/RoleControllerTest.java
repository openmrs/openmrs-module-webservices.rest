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
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.resource.RoleResource;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class RoleControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	/**
	//	 * @see RoleController#create(SimpleObject, javax.servlet.http.HttpServletRequest, HttpServletResponse)
	//	 * @verifies create a new Role
	//	 */
	//	@Test
	//	public void createRole_shouldCreateANewRole() throws Exception {
	//		int before = Context.getUserService().getAllRoles().size();
	//		String json = "{\"name\":\"test\",\"description\":\"This is a test role for RoleControllerTest.\"}}";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object newRole = new RoleController().create(post, new MockHttpServletRequest(), new MockHttpServletResponse());
	//		Assert.assertEquals(before + 1, Context.getUserService().getAllRoles().size());
	//	}
	//	
	//	/**
	//	 * @see RoleController#retrieve(String, javax.servlet.http.HttpServletRequest)
	//	 * @verifies get a default representation of a Role
	//	 */
	//	@Test
	//	public void getRole_shouldGetADefaultRepresentationOfARole() throws Exception {
	//		Object result = new RoleController().retrieve("3480cb6d-c291-46c8-8d3a-96dc33d199fb", new MockHttpServletRequest());
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals("3480cb6d-c291-46c8-8d3a-96dc33d199fb", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "name"));
	//		Assert.assertEquals("Provider", PropertyUtils.getProperty(result, "name"));
	//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	/**
	//	 * @see RoleController#retrieve(String, javax.servlet.http.HttpServletRequest)
	//	 * @verifies get a full representation of a Role
	//	 */
	//	@Test
	//	public void getRole_shouldGetAFullRepresentationOfARole() throws Exception {
	//		MockHttpServletRequest req = new MockHttpServletRequest();
	//		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//		Object result = new RoleController().retrieve("3480cb6d-c291-46c8-8d3a-96dc33d199fb", req);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals("3480cb6d-c291-46c8-8d3a-96dc33d199fb", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "name"));
	//		Assert.assertEquals("Provider", PropertyUtils.getProperty(result, "name"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	/**
	//	 * @see RoleController#update(String, SimpleObject, javax.servlet.http.HttpServletRequest, HttpServletResponse)
	//	 * @verifies change a property on a Role
	//	 */
	//	@Test
	//	public void updateRole_shouldChangeAPropertyOnARole() throws Exception {
	//		Role role = new RoleResource().getByUniqueId("3480cb6d-c291-46c8-8d3a-96dc33d199fb");
	//		Assert.assertFalse("Updated Description".equals(role.getDescription()));
	//		SimpleObject post = new ObjectMapper().readValue("{\"description\":\"Updated Description\"}", SimpleObject.class);
	//		Object editedRole = new RoleController().update("3480cb6d-c291-46c8-8d3a-96dc33d199fb", post,
	//		    new MockHttpServletRequest(), new MockHttpServletResponse());
	//		role = new RoleResource().getByUniqueId("3480cb6d-c291-46c8-8d3a-96dc33d199fb");
	//		Assert.assertEquals("Updated Description", role.getDescription());
	//	}
	//	
	//	/**
	//	 * @see RoleController#delete(String, String, javax.servlet.http.HttpServletRequest, HttpServletResponse)
	//	 * @verifies void a Role
	//	 */
	//	@Test
	//	public void retireRole_shouldRetireARole() throws Exception {
	//		Role role = new RoleResource().getByUniqueId("3480cb6d-c291-46c8-8d3a-96dc33d199fb");
	//		Assert.assertFalse(role.isRetired());
	//		new RoleController().delete("3480cb6d-c291-46c8-8d3a-96dc33d199fb", "unit test", new MockHttpServletRequest(),
	//		    new MockHttpServletResponse());
	//		role = new RoleResource().getByUniqueId("3480cb6d-c291-46c8-8d3a-96dc33d199fb");
	//		Assert.assertTrue(role.isRetired());
	//		Assert.assertEquals("unit test", role.getRetireReason());
	//	}
	//	
	//	/**
	//	 * @see RoleController#search(String, javax.servlet.http.HttpServletRequest, HttpServletResponse)
	//	 * @verifies return no results if there are no matching Roles
	//	 */
	//	@Test
	//	public void findRoles_shouldReturnNoResultsIfThereAreNoMatchingRoles() throws Exception {
	//		List<Object> results = (List<Object>) new RoleController().search("zzzznothing", new MockHttpServletRequest(),
	//		    new MockHttpServletResponse()).get("results");
	//		Assert.assertEquals(0, results.size());
	//	}
	//	
	//	/**
	//	 * @see RoleController#search(String, javax.servlet.http.HttpServletRequest, HttpServletResponse)
	//	 * @verifies find matching Roles
	//	 */
	//	@Test
	//	@Ignore("Roles do not support searching yet.")
	//	public void findRoles_shouldFindMatchingRoles() throws Exception {
	//		List<Object> results = (List<Object>) new UserController().search("Provider", new MockHttpServletRequest(),
	//		    new MockHttpServletResponse()).get("results");
	//		Assert.assertEquals(1, results.size());
	//		Object result = results.get(0);
	//		Assert.assertEquals("3480cb6d-c291-46c8-8d3a-96dc33d199fb", PropertyUtils.getProperty(result, "uuid"));
	//	}
	//	
	//	/**
	//	 * @see RoleController#getAll(javax.servlet.http.HttpServletRequest, HttpServletResponse)
	//	 * @verifies get all Roles
	//	 */
	//	@Test
	//	public void shouldListAllRoles() throws Exception {
	//		int totalCount = Context.getUserService().getAllRoles().size();
	//		
	//		SimpleObject result = new RoleController().getAll(new MockHttpServletRequest(), new MockHttpServletResponse());
	//		
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals(totalCount, Util.getResultsSize(result));
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
