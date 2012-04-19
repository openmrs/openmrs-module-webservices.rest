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

import org.openmrs.module.webservices.rest.web.v1_0.controller.PersonController;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.resource.PersonResource;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.WebRequest;

public class PersonControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private MockHttpServletRequest emptyRequest() {
		return new MockHttpServletRequest();
	}
	
	/**
	 * @see PersonController#createPerson(SimpleObject,WebRequest)
	 * @verifies create a new Person
	 */
	@Test
	@Ignore("RESTWS-241: Define creatable/updatable properties on Person, PersonAddress, and PersonName resources")
	public void createPerson_shouldCreateANewPerson() throws Exception {
		int before = Context.getPersonService().getPeople("", false).size();
		String json = "{ \"preferredName\":{ \"givenName\":\"Helen\", \"familyName\":\"of Troy\" }, \"birthdate\":\"1200-01-01\", \"gender\":\"F\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object beautifulPerson = new PersonController().create(post, emptyRequest(), new MockHttpServletResponse());
		Util.log("Created person", beautifulPerson);
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
		Util.log("Person fetched (default)", result);
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
		Object result = new PersonController().retrieve("5946f880-b197-400b-9caa-a3c661d23041", req);
		Util.log("Person fetched (full)", result);
		Assert.assertNotNull(result);
		Assert.assertEquals("5946f880-b197-400b-9caa-a3c661d23041", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	/**
	 * @see PersonController#updatePerson(Person,SimpleObject,WebRequest)
	 * @verifies change a property on a person
	 */
	@Test
	@Ignore("RESTWS-241: Define creatable/updatable properties on Person, PersonAddress, and PersonName resources")
	public void updatePerson_shouldChangeAPropertyOnAPerson() throws Exception {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleObject post = new ObjectMapper().readValue("{\"birthdate\":\"" + df.format(now) + "\"}", SimpleObject.class);
		Object editedPerson = new PersonController().update("5946f880-b197-400b-9caa-a3c661d23041", post, emptyRequest(),
		    new MockHttpServletResponse());
		Util.log("Edited person", editedPerson);
		Assert.assertEquals(df.format(now), df.format(Context.getPersonService().getPerson(7).getBirthdate()));
	}
	
	/**
	 * DOES NOT WORK YET BECAUSE WE DON'T HAVE A CONVERTER FOR CONCEPTS
	 * 
	 * WE SHOULD HAVE IT NOW
	 * 
	 * @see PersonController#updatePerson(String,SimpleObject,WebRequest)
	 * @verifies change a complex property on a person
	 */
	@Test
	@Ignore("RESTWS-241: Define creatable/updatable properties on Person, PersonAddress, and PersonName resources")
	public void updatePerson_shouldChangeAComplexPropertyOnAPerson() throws Exception {
		SimpleObject post = new ObjectMapper().readValue(
		    "{\"dead\":true, \"causeOfDeath\":\"15f83cd6-64e9-4e06-a5f9-364d3b14a43d\"}", SimpleObject.class);
		Object editedPerson = new PersonController().update("5946f880-b197-400b-9caa-a3c661d23041", post, emptyRequest(),
		    new MockHttpServletResponse());
		Util.log("Set Person as dead", editedPerson);
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
		List<Object> results = (List<Object>) new PersonController().search("zzzznobody", emptyRequest(),
		    new MockHttpServletResponse()).get("results");
		Assert.assertEquals(0, results.size());
	}
	
	/**
	 * @see PersonController#findPersons(String,WebRequest,HttpServletResponse)
	 * @verifies find matching persons
	 */
	@Test
	public void findPersons_shouldFindMatchingPersons() throws Exception {
		List<Object> results = (List<Object>) new PersonController().search("Horatio", emptyRequest(),
		    new MockHttpServletResponse()).get("results");
		Assert.assertEquals(1, results.size());
		Util.log("Found " + results.size() + " person(s)", results);
		Object result = results.get(0);
		Assert.assertEquals("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
	}
	
	@Test
	@Ignore("RESTWS-241: Define creatable/updatable properties on Person, PersonAddress, and PersonName resources")
	public void shouldSetThePreferredAddress() throws Exception {
		executeDataSet("personAddress-Test.xml");
		String personUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		Person person = Context.getPersonService().getPersonByUuid(personUuid);
		Assert.assertFalse(person.getPersonAddress().isPreferred());
		String json = "{ \"preferredAddress\":\"3350d0b5-821c-4e5e-ad1d-a9bce331e118\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		new PersonController().update(personUuid, post, new MockHttpServletRequest(), new MockHttpServletResponse());
		Assert.assertTrue(person.getPersonAddress().isPreferred());
		Assert.assertEquals("1050 Wishard Blvd.", person.getPersonAddress().getAddress1());
	}
	
	@Test
	@Ignore("RESTWS-241: Define creatable/updatable properties on Person, PersonAddress, and PersonName resources")
	public void shouldAddTheAddressIfThePreferredAddressBeingSetIsNew() throws Exception {
		executeDataSet("personAddress-Test.xml");
		String personUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		Person person = Context.getPersonService().getPersonByUuid(personUuid);
		Assert.assertFalse(person.getPersonAddress().isPreferred());
		String json = "{\"preferredAddress\":{ \"address1\":\"test address\", \"country\":\"USA\" }}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		new PersonController().update(personUuid, post, new MockHttpServletRequest(), new MockHttpServletResponse());
		Assert.assertTrue(person.getPersonAddress().isPreferred());
		Assert.assertEquals("test address", person.getPersonAddress().getAddress1());
	}
	
	@Test
	@Ignore("RESTWS-241: Define creatable/updatable properties on Person, PersonAddress, and PersonName resources")
	public void shouldUnmarkTheOldPreferredAddressAsPreferredWhenSettingANewPreferredAddress() throws Exception {
		executeDataSet("personAddress-Test.xml");
		String personUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		Person person = Context.getPersonService().getPersonByUuid(personUuid);
		//set a preferred address for testing purposes
		PersonAddress oldPreferredAddress = person.getPersonAddress();
		oldPreferredAddress.setPreferred(true);
		Context.getPersonService().savePerson(person);
		Assert.assertTrue(person.getPersonAddress().isPreferred());
		String json = "{\"preferredAddress\":{ \"address1\":\"test address\", \"country\":\"USA\" }}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		new PersonController().update(personUuid, post, new MockHttpServletRequest(), new MockHttpServletResponse());
		Assert.assertFalse(oldPreferredAddress.isPreferred());
	}
	
	/**
	 * Tests if voided names are not shown in full representation.
	 * Adds a name and shows full representation. Then voids the name and sees decrease in number of names in 
	 * full representation.
	 * @see PersonController#getProperty(Person,String)
	 * @verifies do not show voided names in full representation
	 * 
	 * @throws Exception 
	 */
	@Test
	public void shouldNotShowVoidedNamesInFullRepresentation() throws Exception {
		Person p = new PersonResource().getByUniqueId("5946f880-b197-400b-9caa-a3c661d23041");
		PersonName pn = new PersonName("SUNNY", "TEST", "PURKAYASTHA");
		p.addName(pn);
		Context.getPersonService().savePerson(p);
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		Object result = new PersonController().retrieve("5946f880-b197-400b-9caa-a3c661d23041", req);
		Assert.assertEquals(2, StringUtils.countMatches(PropertyUtils.getProperty(result, "names").toString(), "givenName"));
		pn.setVoided(true);
		Context.getPersonService().savePerson(p);
		result = new PersonController().retrieve("5946f880-b197-400b-9caa-a3c661d23041", req);
		Assert.assertEquals(1, StringUtils.countMatches(PropertyUtils.getProperty(result, "names").toString(), "givenName"));
	}
	
	/**
	 * Tests if voided attributes are not shown in representation.
	 * Voids all the person attributes and checks if they are not shown
	 * @see PersonController#getProperty(Person,String)
	 * @verifies do not show voided attributes
	 * 
	 * @throws Exception 
	 */
	@Test
	public void shouldNotShowVoidedAttributesInRepresentation() throws Exception {
		Person p = new PersonResource().getByUniqueId("5946f880-b197-400b-9caa-a3c661d23041");
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		Object result = new PersonController().retrieve("5946f880-b197-400b-9caa-a3c661d23041", req);
		Assert.assertEquals(3, ((Collection<?>) PropertyUtils.getProperty(result, "attributes")).size());
		Set<PersonAttribute> attributes = p.getAttributes();
		for (PersonAttribute pa : attributes) {
			pa.setVoided(true);
		}
		p.setAttributes(attributes);
		Context.getPersonService().savePerson(p);
		result = new PersonController().retrieve("5946f880-b197-400b-9caa-a3c661d23041", req);
		Assert
		        .assertEquals(0, StringUtils.countMatches(PropertyUtils.getProperty(result, "attributes").toString(),
		            "value"));
	}
	
	@Test
	public void shouldRespectStartIndexAndLimit() throws Exception {
		MockHttpServletRequest hsr = new MockHttpServletRequest("GET",
		        "http://localhost:8080/openmrs/ws/rest/patient?q=Test");
		SimpleObject wrapper = new PersonController().search("Test", hsr, new MockHttpServletResponse());
		Util.log("Everything", wrapper);
		List<Object> results = (List<Object>) wrapper.get("results");
		int fullCount = results.size();
		Assert.assertTrue("This test assumes >2 matching patients", fullCount > 2);
		
		hsr.removeAllParameters();
		hsr.setParameter(RestConstants.REQUEST_PROPERTY_FOR_LIMIT, "2");
		wrapper = new PersonController().search("Test", hsr, new MockHttpServletResponse());
		Util.log("First 2", wrapper);
		results = (List<Object>) wrapper.get("results");
		int firstCount = results.size();
		Assert.assertEquals(2, firstCount);
		
		hsr.removeAllParameters();
		hsr.setParameter(RestConstants.REQUEST_PROPERTY_FOR_START_INDEX, "2");
		wrapper = new PersonController().search("Test", hsr, new MockHttpServletResponse());
		Util.log("The rest", wrapper);
		results = (List<Object>) wrapper.get("results");
		int restCount = results.size();
		Assert.assertEquals(fullCount, firstCount + restCount);
	}
	
}
