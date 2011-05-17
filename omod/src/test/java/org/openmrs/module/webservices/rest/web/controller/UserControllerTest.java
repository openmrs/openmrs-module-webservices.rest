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
import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.UserAndPassword;
import org.openmrs.module.webservices.rest.web.resource.UserResource;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

public class UserControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private void log(String label, Object object) {
		String toPrint;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.getSerializationConfig().set(SerializationConfig.Feature.INDENT_OUTPUT, true);
			toPrint = mapper.writeValueAsString(object);
		} catch (Exception ex) {
			toPrint = "" + object;
		}
		if (label != null)
			toPrint = label + ": " + toPrint;
		System.out.println(toPrint);
	}
	
	private WebRequest emptyRequest() {
		return new ServletWebRequest(new MockHttpServletRequest());
	}
    
	/**
	 * @see UserController#createUser(SimpleObject,WebRequest)
	 * @verifies create a new user
	 */
	@Test
	public void createUser_shouldCreateANewUser() throws Exception {
		int before = Context.getUserService().getAllUsers().size();
		String json = "{\"username\":\"test\",\"password\":\"Admin@123\",\"person\":\"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\"}}";
        SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object newUser = new UserController().create(post, emptyRequest() , new MockHttpServletResponse());
		log("Created User", newUser);
		Assert.assertEquals(before + 1, Context.getUserService().getAllUsers().size());
	}
	
	/**
	 * @see UserController#getUser(UserAndPassword,WebRequest)
	 * @verifies get a default representation of a UserAndPassword
	 */
	@Test
	public void getUser_shouldGetADefaultRepresentationOfAUser() throws Exception {
		Object result = new UserController().retrieve("c98a1558-e131-11de-babe-001e378eb67e", emptyRequest());
		Assert.assertNotNull(result);
		log("User retrieved (default)", result);
		Assert.assertEquals("c98a1558-e131-11de-babe-001e378eb67e", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "username"));
        Assert.assertEquals( "butch", PropertyUtils.getProperty(result, "username"));
		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	/**
     * @see PatientController#getPatient(String,WebRequest)
     * @verifies get a full representation of a patient
     */
    @Test
    public void getUser_shouldGetAFullRepresentationOfAPatient() throws Exception {
    	MockHttpServletRequest req = new MockHttpServletRequest();
    	req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
    	Object result = new UserController().retrieve("c1d8f5c2-e131-11de-babe-001e378eb67e", new ServletWebRequest(req));
  		log("User retrieved (full)", result);
		Assert.assertNotNull(result);
		Assert.assertEquals("c1d8f5c2-e131-11de-babe-001e378eb67e", PropertyUtils.getProperty(result, "uuid"));
        Assert.assertNotNull(PropertyUtils.getProperty(result, "secretQuestion"));
        Assert.assertEquals( "", PropertyUtils.getProperty(result, "secretQuestion"));
    }
	
	/**
	 * @see UserController#updateUser(UserAndPassword,SimpleObject,WebRequest)
	 * @verifies change a property on a patient
	 */
	@Test
    @Ignore
	public void updateUser_shouldChangeAPropertyOnAUser() throws Exception {
        SimpleObject post = new ObjectMapper().readValue("{\"systemId\":\"5-6\",\"password\":\"Admin@123\"}", SimpleObject.class);
		Object editedUser = new UserController().update("1010d442-e134-11de-babe-001e378eb67e", post, emptyRequest(), new MockHttpServletResponse());
		log("Edited SystemId", editedUser);
        Assert.assertEquals( "5-6", PropertyUtils.getProperty(editedUser, "systemId"));
	}
	
	/**
	 * @see UserController#retireUser(User,String,WebRequest)
	 * @verifies void a patient
	 */
	@Test
    @Ignore
	public void retireUser_shouldRetireAUser() throws Exception {
		UserAndPassword user = new UserResource().getByUniqueId( "c98a1558-e131-11de-babe-001e378eb67e" );
		Assert.assertFalse(user.isRetired());
		new UserController().delete("c98a1558-e131-11de-babe-001e378eb67e", "unit test", emptyRequest(),
		    new MockHttpServletResponse());
        user = new UserResource().getByUniqueId( "c98a1558-e131-11de-babe-001e378eb67e" );
		Assert.assertTrue(user.isRetired());
		Assert.assertEquals("unit test", user.getRetireReason());
	}

	/**
     * @see UserController#findUsers(String,WebRequest,HttpServletResponse)
     * @verifies return no results if there are no matching users
     */
    @Test
    public void findPatients_shouldReturnNoResultsIfThereAreNoMatchingPatients() throws Exception {
	    List<Object> results = new UserController().search("zzzznobody", emptyRequest(), new MockHttpServletResponse());
	    Assert.assertEquals(0, results.size());
    }

	/**
     * @see UserController#findUsers(String,WebRequest,HttpServletResponse)
     * @verifies find matching users
     */
    @Test
    public void findUsers_shouldFindMatchingUsers() throws Exception {
	    List<Object> results = new UserController().search("but", emptyRequest(), new MockHttpServletResponse());
	    Assert.assertEquals(1, results.size());
	    log("Found " + results.size() + " user(s)", results);
	    Object result = results.get(0);
	    Assert.assertEquals("c98a1558-e131-11de-babe-001e378eb67e", PropertyUtils.getProperty(result, "uuid"));
    }
    
}
