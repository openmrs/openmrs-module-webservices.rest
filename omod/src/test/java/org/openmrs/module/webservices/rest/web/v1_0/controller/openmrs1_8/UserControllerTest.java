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
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.UserAndPassword;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.UserResource;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.WebRequest;

public class UserControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	private MockHttpServletRequest emptyRequest() {
	//		return new MockHttpServletRequest();
	//	}
	//	
	//	/**
	//	 * @see UserController#createUser(SimpleObject,WebRequest)
	//	 * @verifies create a new user
	//	 */
	//	@Test
	//	public void createUser_shouldCreateANewUser() throws Exception {
	//		int before = Context.getUserService().getAllUsers().size();
	//		String json = "{\"username\":\"test\",\"password\":\"Admin@123\",\"person\":\"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\"}}";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object newUser = new UserController().create(post, emptyRequest(), new MockHttpServletResponse());
	//		Util.log("Created User", newUser);
	//		Assert.assertEquals(before + 1, Context.getUserService().getAllUsers().size());
	//	}
	//	
	//	/**
	//	 * @see UserController#createUser(SimpleObject,WebRequest)
	//	 * @verifies create a new user
	//	 */
	//	@Test
	//	public void createUser_shouldCreateANewUserWithRoles() throws Exception {
	//		int before = Context.getUserService().getAllUsers().size();
	//		String json = "{\"username\":\"test\",\"password\":\"Admin@123\",\"person\":\"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\",\"roles\":[\"3480cb6d-c291-46c8-8d3a-96dc33d199fb\"]}";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object newUser = new UserController().create(post, emptyRequest(), new MockHttpServletResponse());
	//		Util.log("Created another user with a role this time", newUser);
	//		Assert.assertEquals(before + 1, Context.getUserService().getAllUsers().size());
	//		User createdUser = Context.getUserService().getUserByUsername("test");
	//		Assert.assertTrue(createdUser.hasRole("Provider"));
	//	}
	//	
	//	/**
	//	 * @see UserController#getUser(UserAndPassword,WebRequest)
	//	 * @verifies get a default representation of a UserAndPassword
	//	 */
	//	@Test
	//	public void getUser_shouldGetADefaultRepresentationOfAUser() throws Exception {
	//		Object result = new UserController().retrieve("c98a1558-e131-11de-babe-001e378eb67e", emptyRequest());
	//		Assert.assertNotNull(result);
	//		Util.log("User retrieved (default)", result);
	//		Assert.assertEquals("c98a1558-e131-11de-babe-001e378eb67e", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "username"));
	//		Assert.assertEquals("butch", PropertyUtils.getProperty(result, "username"));
	//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	/**
	//	 * @see PatientController#getPatient(String,WebRequest)
	//	 * @verifies get a full representation of a patient
	//	 */
	//	@Test
	//	public void getUser_shouldGetAFullRepresentationOfAPatient() throws Exception {
	//		MockHttpServletRequest req = new MockHttpServletRequest();
	//		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//		Object result = new UserController().retrieve("c1d8f5c2-e131-11de-babe-001e378eb67e", req);
	//		Util.log("User retrieved (full)", result);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals("c1d8f5c2-e131-11de-babe-001e378eb67e", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "secretQuestion"));
	//		Assert.assertEquals("", PropertyUtils.getProperty(result, "secretQuestion"));
	//	}
	//	
	//	/**
	//	 * @see UserController#updateUser(UserAndPassword,SimpleObject,WebRequest)
	//	 * @verifies change a property on a patient
	//	 */
	//	@Test
	//	@Ignore("RESTWS-242: Define creatable/updatable properties on UserResource resource")
	//	public void updateUser_shouldChangeAPropertyOnAUser() throws Exception {
	//		UserAndPassword user = new UserResource().getByUniqueId("c98a1558-e131-11de-babe-001e378eb67e");
	//		Assert.assertFalse("5-6".equals(user.getUser().getSystemId()));
	//		SimpleObject post = new ObjectMapper().readValue("{\"systemId\":\"5-6\",\"password\":\"Admin@123\"}",
	//		    SimpleObject.class);
	//		Object editedUser = new UserController().update("c98a1558-e131-11de-babe-001e378eb67e", post, emptyRequest(),
	//		    new MockHttpServletResponse());
	//		Util.log("Edited SystemId", editedUser);
	//		user = new UserResource().getByUniqueId("c98a1558-e131-11de-babe-001e378eb67e");
	//		Assert.assertEquals("5-6", user.getUser().getSystemId());
	//	}
	//	
	//	/**
	//	 * @see UserController#retireUser(User,String,WebRequest)
	//	 * @verifies void a patient
	//	 */
	//	@Test
	//	public void retireUser_shouldRetireAUser() throws Exception {
	//		UserAndPassword user = new UserResource().getByUniqueId("c98a1558-e131-11de-babe-001e378eb67e");
	//		Assert.assertFalse(user.isRetired());
	//		new UserController().delete("c98a1558-e131-11de-babe-001e378eb67e", "unit test", emptyRequest(),
	//		    new MockHttpServletResponse());
	//		user = new UserResource().getByUniqueId("c98a1558-e131-11de-babe-001e378eb67e");
	//		Assert.assertTrue(user.getUser().isRetired());
	//		Assert.assertEquals("unit test", user.getUser().getRetireReason());
	//	}
	//	
	//	/**
	//	 * @see UserController#findUsers(String,WebRequest,HttpServletResponse)
	//	 * @verifies return no results if there are no matching users
	//	 */
	//	@Test
	//	public void findUsers_shouldReturnNoResultsIfThereAreNoMatchingUsers() throws Exception {
	//		List<Object> results = (List<Object>) new UserController().search("zzzznobody", emptyRequest(),
	//		    new MockHttpServletResponse()).get("results");
	//		Assert.assertEquals(0, results.size());
	//	}
	//	
	//	/**
	//	 * @see UserController#findUsers(String,WebRequest,HttpServletResponse)
	//	 * @verifies find matching users
	//	 */
	//	@Test
	//	public void findUsers_shouldFindMatchingUsers() throws Exception {
	//		List<Object> results = (List<Object>) new UserController().search("but", emptyRequest(),
	//		    new MockHttpServletResponse()).get("results");
	//		Assert.assertEquals(1, results.size());
	//		Util.log("Found " + results.size() + " user(s)", results);
	//		Object result = results.get(0);
	//		Assert.assertEquals("c98a1558-e131-11de-babe-001e378eb67e", PropertyUtils.getProperty(result, "uuid"));
	//	}
	//	
	//	@Test
	//	public void shouldListAllUsers() throws Exception {
	//		int totalCount = Context.getUserService().getAllUsers().size();
	//		
	//		SimpleObject result = new UserController().getAll(emptyRequest(), new MockHttpServletResponse());
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals(totalCount, Util.getResultsSize(result));
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
