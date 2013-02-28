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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ResourceTestConstants;
import org.openmrs.util.Format;
import org.openmrs.util.Format.FORMAT_TYPE;
import org.springframework.mock.web.MockHttpServletRequest;

public class EncounterControllerTest extends BaseCrudControllerTest {
	
	public static final String currentTimezone = Calendar.getInstance().getTimeZone().getDisplayName(true, TimeZone.SHORT);
	
	/**
	 * @verifies create a new encounter
	 */
	@Test
	public void createEncounter_shouldCreateANewEncounter() throws Exception {
		int before = Context.getEncounterService().getAllEncounters(null).size();
		String json = "{\"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\", \"encounterType\": \"61ae96f4-6afe-4351-b6f8-cd4fc383cce1\", \"encounterDatetime\": \"2011-01-15\", \"patient\": \"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\", \"provider\":\"ba1b19c2-3ed6-4f63-b8c0-f762dc8d7562\"}";
		
		Object newEncounter = deserialize(handle(newPostRequest(getURI(), json)));
		Assert.assertNotNull(newEncounter);
		Assert.assertEquals(before + 1, Context.getEncounterService().getAllEncounters(null).size());
	}
	
	@Test
	public void createEncounter_shouldCreateANewEncounterWithEmptyUnitOnNumericConcept() throws Exception {
		executeDataSet("customConceptDataset.xml");
		int before = Context.getEncounterService().getAllEncounters(null).size();
		String json = "{\"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\", "
		        + "\"encounterType\": \"61ae96f4-6afe-4351-b6f8-cd4fc383cce1\", "
		        + "\"encounterDatetime\": \"2011-01-15\", \"patient\": \"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\", "
		        + "\"provider\":\"ba1b19c2-3ed6-4f63-b8c0-f762dc8d7562\", "
		        + "\"obs\": [ { \"concept\": \"d102c80f-1yz9-4da3-bb88-8122ce8868dd\", \"value\": 8 } ] }";
		Object newEncounter = deserialize(handle(newPostRequest(getURI(), json)));
		Assert.assertNotNull(newEncounter);
		Assert.assertEquals(before + 1, Context.getEncounterService().getAllEncounters(null).size());
	}
	
	private SimpleObject encounterWithObs() throws Exception {
		List<SimpleObject> obs = new ArrayList<SimpleObject>();
		// weight in kg = 70
		obs.add(SimpleObject.parseJson("{ \"concept\": \"c607c80f-1ea9-4da3-bb88-6276ce8868dd\", \"value\": 70 }"));
		// civil status = married
		obs
		        .add(SimpleObject
		                .parseJson("{ \"concept\": \"89ca642a-dab6-4f20-b712-e12ca4fc6d36\", \"value\": \"92afda7c-78c9-47bd-a841-0de0817027d4\" }"));
		// favorite food, non-coded = fried chicken
		obs.add(SimpleObject
		        .parseJson("{ \"concept\": \"96408258-000b-424e-af1a-403919332938\", \"value\": \"fried chicken\" }"));
		// date of food assistance = 2011-06-21
		obs.add(SimpleObject
		        .parseJson("{ \"concept\": \"11716f9c-1434-4f8d-b9fc-9aa14c4d6126\", \"value\": \"2011-06-21\" }"));
		
		return new SimpleObject().add("location", "9356400c-a5a2-4532-8f2b-2361b3446eb8").add("encounterType",
		    "61ae96f4-6afe-4351-b6f8-cd4fc383cce1").add("encounterDatetime", "2011-01-15").add("patient",
		    "da7f524f-27ce-4bb2-86d6-6d1d05312bd5").add("provider", "ba1b19c2-3ed6-4f63-b8c0-f762dc8d7562").add("obs", obs);
	}
	
	/**
	 * @verifies create a new encounter with obs
	 */
	@Test
	public void createEncounter_shouldCreateANewEncounterWithObs() throws Exception {
		int before = Context.getEncounterService().getAllEncounters(null).size();
		SimpleObject post = encounterWithObs();
		
		SimpleObject newEncounter = deserialize(handle(newPostRequest(getURI(), post)));
		
		Assert.assertNotNull(newEncounter);
		Assert.assertEquals(before + 1, Context.getEncounterService().getAllEncounters(null).size());
		
		Util.log("created encounter with obs", newEncounter);
		List<Map> obs = (List<Map>) newEncounter.get("obs");
		Assert.assertEquals(4, obs.size());
		Set<String> obsDisplayValues = new HashSet<String>();
		for (Map o : obs) {
			obsDisplayValues.add((String) o.get("display"));
		}
		Assert.assertTrue(obsDisplayValues.contains("CIVIL STATUS: MARRIED"));
		Assert.assertTrue(obsDisplayValues.contains("FAVORITE FOOD, NON-CODED: fried chicken"));
		Assert.assertTrue(obsDisplayValues.contains("WEIGHT (KG): 70.0"));
		
		// obs.getValueAsString() uses application Locale and hence have to do this
		Calendar cal = Calendar.getInstance();
		cal.set(2011, Calendar.JUNE, 21, 0, 0, 0);
		String format = Format.format(cal.getTime(), Context.getLocale(), FORMAT_TYPE.TIMESTAMP);
		Assert.assertTrue(obsDisplayValues.contains("DATE OF FOOD ASSISTANCE: " + format));
	}
	
	@Test
	public void shouldCreateAnEncounterWithObsAndOrdersOfDifferentTypes() throws Exception {
		String foodAssistanceUuid = "0dde1358-7fcf-4341-a330-f119241a46e8";
		String lunchOrderUuid = "e23733ab-787e-4096-8ba2-577a902d2c2b";
		String lunchInstructions = "Give them yummy food please";
		String triomuneConceptUuid = "d144d24f-6913-4b63-9660-a9108c2bebef";
		String triomuneDrugUuid = "3cfcf118-931c-46f7-8ff6-7b876f0d4202";
		
		int before = Context.getEncounterService().getAllEncounters(null).size();
		SimpleObject post = encounterWithObs();
		List<SimpleObject> orders = new ArrayList<SimpleObject>();
		orders.add(SimpleObject.parseJson("{ \"type\": \"order\", \"concept\": \"" + foodAssistanceUuid
		        + "\", \"orderType\": \"" + lunchOrderUuid + "\", \"instructions\": \"" + lunchInstructions + "\" }"));
		orders.add(SimpleObject.parseJson("{ \"type\": \"drugorder\", \"concept\": \"" + triomuneConceptUuid
		        + "\", \"drug\": \"" + triomuneDrugUuid + "\", \"dose\": \"1\", \"units\": \"tablet\" }"));
		post.add("orders", orders);
		
		SimpleObject newEncounter = deserialize(handle(newPostRequest(getURI(), post)));
		
		Assert.assertNotNull(newEncounter);
		Assert.assertEquals(before + 1, Context.getEncounterService().getAllEncounters(null).size());
		Util.log("created encounter with obs and orders", newEncounter);
		
		List<Map> newOrders = (List<Map>) newEncounter.get("orders");
		Assert.assertEquals(2, newOrders.size());
		List<String> lookFor = new ArrayList<String>(Arrays.asList("FOOD ASSISTANCE", "Triomune-30: 1.0 tablet"));
		for (Map o : newOrders) {
			lookFor.remove(o.get("display"));
		}
		Assert.assertEquals("Did not find: " + lookFor, 0, lookFor.size());
	}
	
	/**
	 * @verifies return no results if there are no matching encounters
	 */
	@Test
	public void findEncounters_shouldReturnNoResultsIfThereAreNoMatchingEncounters() throws Exception {
		SimpleObject object = deserialize(handle(newGetRequest(getURI(), new Parameter("q", "noencounter"))));
		List<Object> results = Util.getResultsList(object);
		Assert.assertEquals(0, results.size());
	}
	
	/**
	 * @verifies get a full representation of a encounter including obs groups
	 */
	@Test
	public void getEncounter_shouldGetAFullRepresentationOfAEncounterIncludingObsGroups() throws Exception {
		executeDataSet("EncounterWithObsGroup.xml");
		
		SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		
		SimpleObject result = deserialize(handle(newGetRequest(getURI() + "/" + "62967e68-96bb-11e0-8d6b-9b9415a91465",
		    new Parameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL))));
		
		Assert.assertNotNull(result);
		Assert.assertEquals("62967e68-96bb-11e0-8d6b-9b9415a91465", result.get("uuid"));
		Assert.assertNotNull(result.get("obs"));
		Assert.assertEquals("0f97e14e-cdc2-49ac-9255-b5126f8a5147", Util.getByPath(result, "obs[0]/concept/uuid"));
		Assert.assertEquals("96408258-000b-424e-af1a-403919332938", Util.getByPath(result,
		    "obs[0]/groupMembers[0]/concept/uuid"));
		Assert.assertEquals("Some text", Util.getByPath(result, "obs[0]/groupMembers[0]/value"));
		Assert.assertEquals("11716f9c-1434-4f8d-b9fc-9aa14c4d6126", Util.getByPath(result,
		    "obs[0]/groupMembers[1]/concept/uuid"));
		Assert.assertEquals(ConversionUtil.convertToRepresentation(ymd.parse("2011-06-12"), Representation.DEFAULT), Util
		        .getByPath(result, "obs[0]/groupMembers[1]/value"));
		// make sure there's a group in the group
		Assert.assertEquals("0f97e14e-cdc2-49ac-9255-b5126f8a5147", Util.getByPath(result,
		    "obs[0]/groupMembers[2]/concept/uuid"));
		Assert.assertEquals("96408258-000b-424e-af1a-403919332938", Util.getByPath(result,
		    "obs[0]/groupMembers[2]/groupMembers[0]/concept/uuid"));
		Assert.assertEquals("Some text", Util.getByPath(result, "obs[0]/groupMembers[2]/groupMembers[0]/value"));
		Assert.assertEquals("11716f9c-1434-4f8d-b9fc-9aa14c4d6126", Util.getByPath(result,
		    "obs[0]/groupMembers[2]/groupMembers[1]/concept/uuid"));
		Assert.assertEquals(ConversionUtil.convertToRepresentation(ymd.parse("2011-06-12"), Representation.DEFAULT), Util
		        .getByPath(result, "obs[0]/groupMembers[2]/groupMembers[1]/value"));
	}
	
	/**
	 * @verifies fail to purge a encounter with dependent data
	 */
	@Test(expected = ConstraintViolationException.class)
	public void purgeEncounter_shouldNotPurgeAEncounterWithDependentData() throws Exception {
		int size = Context.getEncounterService().getEncountersByPatient(new Patient(7)).size();
		handle(newDeleteRequest(getURI() + "/6519d653-393b-4118-9c83-a3715b82d4ac", new Parameter("purge", "")));
		
		Assert.assertEquals(size - 1, Context.getEncounterService().getEncountersByPatient(new Patient(7)).size());
	}
	
	/**
	 * @verifies change a property on a encounter
	 */
	@Test
	public void updateEncounter_shouldChangeAPropertyOnAEncounter() throws Exception {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleObject post = new ObjectMapper().readValue("{\"encounterDatetime\":\"" + df.format(now) + "\"}",
		    SimpleObject.class);
		handle(newPostRequest(getURI() + "/6519d653-393b-4118-9c83-a3715b82d4ac", post));
		Assert.assertEquals(df.format(now), df.format(Context.getEncounterService().getEncounter(3).getEncounterDatetime()));
	}
	
	/**
	 * @verifies void a encounter
	 */
	@Test
	public void voidEncounter_shouldVoidAEncounter() throws Exception {
		Encounter enc = Context.getEncounterService().getEncounter(3);
		Assert.assertFalse(enc.isVoided());
		handle(newDeleteRequest(getURI() + "/6519d653-393b-4118-9c83-a3715b82d4ac", new Parameter("reason", "unit test")));
		enc = Context.getEncounterService().getEncounter(3);
		Assert.assertTrue(enc.isVoided());
		Assert.assertEquals("unit test", enc.getVoidReason());
	}
	
	/**
	 * @verifies return a list of encounters
	 */
	@Test
	public void searchByPatient_shouldReturnAListOfEncounters() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		
		Object result = deserialize(handle(newGetRequest(getURI(), new Parameter("patient",
		        "5946f880-b197-400b-9caa-a3c661d23041"))));
		Assert.assertNotNull(result);
		List encList = (List) PropertyUtils.getProperty(result, "results");
		Assert.assertNotNull(encList);
		Assert.assertTrue(encList.size() > 0);
		List<String> uuids = new ArrayList<String>();
		for (Object encounter : encList) {
			uuids.add((String) PropertyUtils.getProperty(encounter, "uuid"));
		}
		Assert.assertTrue(uuids.contains("6519d653-393b-4118-9c83-a3715b82d4ac"));
	}
	
	@Test
	public void search_shouldSearchForEncountersByASearchPhrase() throws Exception {
		SimpleObject response = deserialize(handle(newGetRequest(getURI(), new Parameter("q", "Collet"))));
		List<Object> results = Util.getResultsList(response);
		Assert.assertNotNull(results);
		Assert.assertEquals(3, results.size());
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#shouldGetAll()
	 */
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "encounter";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return ResourceTestConstants.ENCOUNTER_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return 0; //Not supported
	}
}
