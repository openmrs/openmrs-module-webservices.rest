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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

public class PersonControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private void log(String label, Object object) {
		String toPrint;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.getSerializationConfig().set(SerializationConfig.Feature.INDENT_OUTPUT, true);
			toPrint = mapper.writeValueAsString(object);
		}
		catch (Exception ex) {
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
	 * @see PersonController#createPerson(SimpleObject,WebRequest)
	 * @verifies create a new Person
	 */
	@Test
	public void createPerson_shouldCreateANewPerson() throws Exception {
		int before = Context.getPersonService().getPeople("", false).size();
		String json = "{ \"preferredName\":{ \"givenName\":\"Helen\", \"familyName\":\"of Troy\" }, \"birthdate\":\"1200-01-01\", \"gender\":\"F\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object beautifulPerson = new PersonController().create(post, emptyRequest(), new MockHttpServletResponse());
		log("Created person", beautifulPerson);
		Assert.assertEquals(before + 1, Context.getPersonService().getPeople("", false).size());
	}
	
	/**
	 * @see PersonController#getPerson(Person,WebRequest)
	 * @verifies get a default representation of a person
	 */
	@Test
	public void getPerson_shouldGetADefaultRepresentationOfAPerson() throws Exception {
		Object result = new PersonController().retrieve("5946f880-b197-400b-9caa-a3c661d23041", emptyRequest());
		Assert.assertNotNull(result);
		log("Person fetched (default)", result);
		Assert.assertEquals("5946f880-b197-400b-9caa-a3c661d23041", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "preferredName"));
		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	/**
	 * @see PersonController#getPerson(String,WebRequest)
	 * @verifies get a full representation of a person
	 */
	@Test
	public void getPerson_shouldGetAFullRepresentationOfAPerson() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		Object result = new PersonController().retrieve("5946f880-b197-400b-9caa-a3c661d23041", new ServletWebRequest(req));
		log("Person fetched (full)", result);
		Assert.assertNotNull(result);
		Assert.assertEquals("5946f880-b197-400b-9caa-a3c661d23041", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	/**
	 * @see PersonController#updatePerson(Person,SimpleObject,WebRequest)
	 * @verifies change a property on a person
	 */
	@Test
	public void updatePerson_shouldChangeAPropertyOnAPerson() throws Exception {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleObject post = new ObjectMapper().readValue("{\"birthdate\":\"" + df.format(now) + "\"}", SimpleObject.class);
		Object editedPerson = new PersonController().update("5946f880-b197-400b-9caa-a3c661d23041", post, emptyRequest(),
		    new MockHttpServletResponse());
		log("Edited person", editedPerson);
		Assert.assertEquals(df.format(now), df.format(Context.getPersonService().getPerson(7).getBirthdate()));
	}
	
	/**
	 * DOES NOT WORK YET BECAUSE WE DON'T HAVE A CONVERTER FOR CONCEPTS
	 * @see PersonController#updatePerson(String,SimpleObject,WebRequest)
	 * @verifies change a complex property on a person
	 */
	@Test
	@Ignore
	public void updatePerson_shouldChangeAComplexPropertyOnAPerson() throws Exception {
		SimpleObject post = new ObjectMapper().readValue(
		    "{\"dead\":true, \"causeOfDeath\":\"15f83cd6-64e9-4e06-a5f9-364d3b14a43d\"}", SimpleObject.class);
		Object editedPerson = new PersonController().update("5946f880-b197-400b-9caa-a3c661d23041", post, emptyRequest(),
		    new MockHttpServletResponse());
		log("Set Person as dead", editedPerson);
		Assert.assertEquals(new Concept(88), PropertyUtils.getProperty(editedPerson, "causeOfDeath"));
	}
	
	/**
	 * @see PersonController#voidPerson(Person,String,WebRequest)
	 * @verifies void a person
	 */
	@Test
	public void voidPerson_shouldVoidAPerson() throws Exception {
		Person person = Context.getPersonService().getPerson(7);
		Assert.assertFalse(person.isVoided());
		new PersonController().delete("5946f880-b197-400b-9caa-a3c661d23041", "test delete", emptyRequest(),
		    new MockHttpServletResponse());
		person = Context.getPersonService().getPerson(7);
		Assert.assertTrue(person.isVoided());
		Assert.assertEquals("test delete", person.getVoidReason());
	}
	
	/**
	 * @see PersonController#findPersons(String,WebRequest,HttpServletResponse)
	 * @verifies return no results if there are no matching person(s)
	 */
	@Test
	public void findPersons_shouldReturnNoResultsIfThereAreNoMatchingPersons() throws Exception {
		List<Object> results = new PersonController().search("zzzznobody", emptyRequest(), new MockHttpServletResponse());
		Assert.assertEquals(0, results.size());
	}
	
	/**
	 * @see PersonController#findPersons(String,WebRequest,HttpServletResponse)
	 * @verifies find matching persons
	 */
	@Test
	public void findPersons_shouldFindMatchingPersons() throws Exception {
		List<Object> results = new PersonController().search("Horatio", emptyRequest(), new MockHttpServletResponse());
		Assert.assertEquals(1, results.size());
		log("Found " + results.size() + " person(s)", results);
		Object result = results.get(0);
		Assert.assertEquals("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "uri"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
	}
	
}
