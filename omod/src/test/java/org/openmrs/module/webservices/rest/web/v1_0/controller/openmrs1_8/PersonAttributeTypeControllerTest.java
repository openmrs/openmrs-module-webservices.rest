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
import org.junit.Test;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.WebRequest;

public class PersonAttributeTypeControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	private MockHttpServletRequest emptyRequest() {
	//		return new MockHttpServletRequest();
	//	}
	//	
	//	/**
	//	 * @see PersonAttributeTypeController#createPersonAttributeType(SimpleObject,WebRequest)
	//	 * @verifies create a new PersonAttributeType
	//	 */
	//	@Test
	//	public void createPersonAttributeType_shouldCreateANewPersonAttributeType() throws Exception {
	//		int before = Context.getPersonService().getAllPersonAttributeTypes().size();
	//		String json = "{ \"name\":\"Some attributeType\",\"description\":\"Attribute Type for test\",\"format\":\"java.lang.String\",\"searchable\":false}";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object personAttributeType = new PersonAttributeTypeController().create(post, emptyRequest(),
	//		    new MockHttpServletResponse());
	//		Util.log("Created person attribute type", personAttributeType);
	//		Assert.assertEquals(before + 1, Context.getPersonService().getAllPersonAttributeTypes().size());
	//	}
	//	
	//	/**
	//	 * @see PersonAttributeTypeController#getPersonAttributeType(PersonAttributeType,WebRequest)
	//	 * @verifies get a default representation of a person attribute type
	//	 */
	//	@Test
	//	public void getPersonAttributeType_shouldGetADefaultRepresentationOfAPersonAttributeType() throws Exception {
	//		Object result = new PersonAttributeTypeController().retrieve("b3b6d540-a32e-44c7-91b3-292d97667518", emptyRequest());
	//		Assert.assertNotNull(result);
	//		Util.log("Person fetched (default)", result);
	//		Assert.assertEquals("b3b6d540-a32e-44c7-91b3-292d97667518", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "name"));
	//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	/**
	//	 * @see PersonAttributeTypeController#getPersonAttributeType(String,WebRequest)
	//	 * @verifies get a full representation of a person attribute type
	//	 */
	//	@Test
	//	public void getPersonAttributeType_shouldGetAFullRepresentationOfAPersonAttributeType() throws Exception {
	//		MockHttpServletRequest req = new MockHttpServletRequest();
	//		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//		Object result = new PersonAttributeTypeController().retrieve("b3b6d540-a32e-44c7-91b3-292d97667518", req);
	//		Util.log("Person fetched (full)", result);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals("b3b6d540-a32e-44c7-91b3-292d97667518", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	/**
	//	 * @see PersonAttributeTypeController#updatePersonAttributeType(PersonAttributeType,SimpleObject,WebRequest)
	//	 * @verifies change a property on a person
	//	 */
	//	@Test
	//	public void updatePersonAttributeType_shouldChangeAPropertyOnAPersonAttributeType() throws Exception {
	//		SimpleObject post = new ObjectMapper().readValue("{\"description\":\"Updated description\"}", SimpleObject.class);
	//		Object editedPersonAttributeType = new PersonAttributeTypeController().update(
	//		    "b3b6d540-a32e-44c7-91b3-292d97667518", post, emptyRequest(), new MockHttpServletResponse());
	//		Util.log("Edited person", editedPersonAttributeType);
	//		Assert.assertEquals("Updated description", Context.getPersonService().getPersonAttributeType(1).getDescription());
	//	}
	//	
	//	/**
	//	 * @see PersonAttributeTypeController#retirePersonAttributeType(PersonAttributeType,String,WebRequest)
	//	 * @verifies void a person attribute type
	//	 */
	//	@Test
	//	public void retirePersonAttributeType_shouldRetireAPersonAttributeType() throws Exception {
	//		PersonAttributeType personAttributeType = Context.getPersonService().getPersonAttributeType(2);
	//		Assert.assertFalse(personAttributeType.isRetired());
	//		new PersonAttributeTypeController().delete("54fc8400-1683-4d71-a1ac-98d40836ff7c", "test", emptyRequest(),
	//		    new MockHttpServletResponse());
	//		personAttributeType = Context.getPersonService().getPersonAttributeType(1);
	//		Assert.assertTrue(personAttributeType.isRetired());
	//		Assert.assertEquals("test", personAttributeType.getRetireReason());
	//	}
	//	
	//	/**
	//	 * @see PersonAttributeTypeController#findPersonAttributeTypes(String,WebRequest,HttpServletResponse)
	//	 * @verifies return no results if there are no matching person(s)
	//	 */
	//	@Test
	//	public void findPersonAttributeTypes_shouldReturnNoResultsIfThereAreNoMatchingPersons() throws Exception {
	//		List<Object> results = (List<Object>) new PersonAttributeTypeController().search("zzzznotype", emptyRequest(),
	//		    new MockHttpServletResponse()).get("results");
	//		Assert.assertEquals(0, results.size());
	//	}
	//	
	//	/**
	//	 * @see PersonAttributeTypeController#findPersonAttributeTypes(String,WebRequest,HttpServletResponse)
	//	 * @verifies find matching person attribute types
	//	 */
	//	@Test
	//	public void findPersonAttributeTypes_shouldFindMatchingPersonAttributeTypes() throws Exception {
	//		List<Object> results = (List<Object>) new PersonAttributeTypeController().search("Birthplace", emptyRequest(),
	//		    new MockHttpServletResponse()).get("results");
	//		Assert.assertEquals(1, results.size());
	//		Util.log("Found " + results.size() + " personAttributeType(s)", results);
	//		Object result = results.get(0);
	//		Assert.assertEquals("54fc8400-1683-4d71-a1ac-98d40836ff7c", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
