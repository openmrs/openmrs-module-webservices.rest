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
import org.junit.Test;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.UserAndPassword1_8;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

public class UserController1_8Test extends MainResourceControllerTest {
	
	private UserService service;
	
	@Before
	public void init() {
		service = Context.getUserService();
	}
	
	/**
	 * @see UserController#createUser(SimpleObject,WebRequest)
	 * @throws Exception 
	 * @verifies create a new user
	 */
	@Test
	public void createUser_shouldCreateANewUser() throws Exception {
		
		long originalCount = getAllCount();
		
		SimpleObject user = new SimpleObject();
		user.add("username", "test user");
		user.add("password", "Secret123");
		user.add("person", "da7f524f-27ce-4bb2-86d6-6d1d05312bd5");
		
		String json = new ObjectMapper().writeValueAsString(user);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		SimpleObject newUser = deserialize(handle(req));
		
		Util.log("Created User", newUser);
		Assert.assertNotNull(PropertyUtils.getProperty(newUser, "uuid"));
		Assert.assertEquals(originalCount + 1, getAllCount());
	}
	
	/**
	 * @see UserController#createUser(SimpleObject,WebRequest)
	 * @throws Exception 
	 * @verifies create a new user
	 */
	@Test
	public void createUser_shouldCreateANewUserWithRoles() throws Exception {
		
		long originalCount = getAllCount();
		
		SimpleObject user = new SimpleObject();
		user.add("username", "test user");
		user.add("password", "Secret123");
		user.add("person", "da7f524f-27ce-4bb2-86d6-6d1d05312bd5");
		user.add("roles", new String[] { "3480cb6d-c291-46c8-8d3a-96dc33d199fb" });
		
		String json = new ObjectMapper().writeValueAsString(user);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		SimpleObject newUser = deserialize(handle(req));
		
		Util.log("Created another user with a role this time.", newUser);
		Assert.assertNotNull(PropertyUtils.getProperty(newUser, "uuid"));
		Assert.assertEquals(originalCount + 1, getAllCount());
		
		User createdUser = service.getUserByUuid(getUuid());
		Assert.assertNotNull(createdUser);
		Assert.assertTrue(createdUser.hasRole("Provider"));
	}
	
	/**
	 * @see UserController#getUser(UserAndPassword1_8,WebRequest)
	 * @throws Exception 
	 * @verifies get a default representation of a UserAndPassword
	 */
	@Test
	public void getUser_shouldGetADefaultRepresentationOfAUser() throws Exception {
		
		final String userName = "butch";
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Util.log("User retrieved (default)", result);
		
		Assert.assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "username"));
		
		Assert.assertEquals(userName, PropertyUtils.getProperty(result, "username"));
		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	/**
	 * @see PatientController#getPatient(String,WebRequest)
	 * @throws Exception 
	 * @verifies get a full representation of a patient
	 */
	@Test
	public void getUser_shouldGetAFullRepresentationOfAPatient() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		
		SimpleObject result = deserialize(handle(req));
		Util.log("User retrieved (full)", result);
		
		Assert.assertNotNull(result);
		Assert.assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
		
		Assert.assertNotNull(PropertyUtils.getProperty(result, "secretQuestion"));
		Assert.assertEquals("", PropertyUtils.getProperty(result, "secretQuestion"));
	}
	
	/**
	 * @see UserController#updateUser(UserAndPassword1_8,SimpleObject,WebRequest)
	 * @throws Exception 
	 * @verifies change a property on a patient
	 */
	@Test
	public void updateUser_shouldChangeAPropertyOnAUser() throws Exception {
		
		User user = service.getUserByUuid(getUuid());
		Assert.assertNotNull(user);
		Assert.assertFalse("5-6".equals(user.getSystemId()));
		Util.log("Old User SystemId: ", user.getSystemId());
		
		String json = "{\"systemId\":\"5-6\",\"password\":\"Admin@123\"}";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/" + getUuid());
		req.setContent(json.getBytes());
		handle(req);
		
		User editedUser = service.getUserByUuid(getUuid());
		Assert.assertNotNull(editedUser);
		Assert.assertEquals("5-6", editedUser.getSystemId());
		Util.log("Edited User SystemId: ", editedUser.getSystemId());
	}
	
	/**
	 * @see UserController#retireUser(User,String,WebRequest)
	 * @throws Exception 
	 * @verifies void a patient
	 */
	@Test
	public void retireUser_shouldRetireAUser() throws Exception {
		
		User user = service.getUserByUuid(getUuid());
		Assert.assertFalse(user.isRetired());
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + user.getUuid());
		req.addParameter("!purge", "");
		req.addParameter("reason", "unit test");
		handle(req);
		
		User retiredUser = service.getUserByUuid(getUuid());
		Assert.assertTrue(retiredUser.isRetired());
		Assert.assertEquals("unit test", retiredUser.getRetireReason());
	}
	
	/**
	 * @see UserController#findUsers(String,WebRequest,HttpServletResponse)
	 * @throws Exception 
	 * @verifies return no results if there are no matching users
	 */
	@Test
	public void findUsers_shouldReturnNoResultsIfThereAreNoMatchingUsers() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("q", "foo-bar-baz");
		
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		
		List<User> hits = (List<User>) result.get("results");
		Assert.assertEquals(0, hits.size());
	}
	
	/**
	 * @see UserController#findUsers(String,WebRequest,HttpServletResponse)
	 * @throws Exception 
	 * @verifies find matching users
	 */
	@Test
	public void findUsers_shouldFindMatchingUsers() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("q", "but");
		
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		
		List<Object> hits = (List<Object>) result.get("results");
		Assert.assertEquals(1, hits.size());
		
		Util.log("Found " + hits.size() + " user(s)", result);
		Assert.assertEquals(service.getUserByUuid(getUuid()).getUuid(), PropertyUtils.getProperty(hits.get(0), "uuid"));
	}
	
	@Test
	public void shouldFindUserByUsername() throws Exception {
		SimpleObject response = deserialize(handle(newGetRequest(getURI(), new Parameter("username", "butch"))));
		List<Object> results = Util.getResultsList(response);
		
		Assert.assertEquals(1, results.size());
		Object next = results.iterator().next();
		
		Util.log("Found " + results.size() + " user(s) by username", response);
		Assert.assertEquals(getUuid(), (String) PropertyUtils.getProperty(next, "uuid"));
	}
	
	@Test
	public void getUser_shouldListAllUsers() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		
		Util.log("Users fetched: ", result);
		Assert.assertNotNull(result);
		
		Util.log("Total users fetched: ", getAllCount());
		Assert.assertEquals(getAllCount(), Util.getResultsSize(result));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "user";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_8.USER_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return service.getAllUsers().size();
	}
}
