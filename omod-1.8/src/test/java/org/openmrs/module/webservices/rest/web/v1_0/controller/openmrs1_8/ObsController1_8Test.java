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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ObsResource1_8;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

public class ObsController1_8Test extends MainResourceControllerTest {
	
	private static final String ACTIVE_LIST_INITIAL_XML = "customActiveListTest.xml";
	
	@Before
	public void init() throws Exception {
		executeDataSet(ACTIVE_LIST_INITIAL_XML);
	}
	
	/*
	 * @verifies get a default representation of a obs
	 */
	@Test
	public void getObs_shouldGetADefaultRepresentationOfAObs() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI() + "/" + RestTestConstants1_8.OBS_UUID)));
		Assert.assertNotNull(result);
		Util.log("Obs fetched (default)", result);
		Assert.assertEquals(RestTestConstants1_8.OBS_UUID, PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "concept"));
	}
	
	/**
	 * @verifies get a full representation of a obs
	 */
	@Test
	public void getObs_shouldGetAFullRepresentationOfAObs() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI() + "/" + RestTestConstants1_8.OBS_UUID, new Parameter(
		        RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL));
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		Util.log("Obs fetched (default)", result);
		Assert.assertEquals(RestTestConstants1_8.OBS_UUID, PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "concept"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	/**
	 * @verifies get a default representation of all obs
	 */
	@Test
	public void getObsByPatientId_shouldGetADefaultRepresentationOfAllObs() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI(),
		    new Parameter("patient", "5946f880-b197-400b-9caa-a3c661d23041"));
		SimpleObject result = deserialize(handle(req));
		List<Object> results = Util.getResultsList(result);
		Assert.assertNotNull(results);
		Object obs = results.get(8);
		Assert.assertEquals(9, results.size());
		Assert.assertNotNull(PropertyUtils.getProperty(obs, "links"));
		Assert.assertNotNull(PropertyUtils.getProperty(obs, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(obs, "display"));
	}
	
	@Test
	public void searchByEncounter_shouldGetObsInAnEncounter() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI(), new Parameter("encounter",
		        "6519d653-393b-4118-9c83-a3715b82d4ac"));
		SimpleObject result = deserialize(handle(req));
		List<Object> results = Util.getResultsList(result);
		Assert.assertEquals(2, results.size());
		List<Object> uuids = Arrays.asList(PropertyUtils.getProperty(results.get(0), "uuid"), PropertyUtils.getProperty(
		    results.get(1), "uuid"));
		
		Assert.assertTrue(uuids.contains("39fb7f47-e80a-4056-9285-bd798be13c63"));
		Assert.assertTrue(uuids.contains("be48cdcb-6a76-47e3-9f2e-2635032f3a9a"));
	}
	
	/**
	 * @verifies create a new obs with numeric concept
	 */
	@Test
	public void createObs_shouldCreateANewObsWithNumericConcept() throws Exception {
		List<Obs> observationsByPerson = Context.getObsService().getObservationsByPerson(
		    (Context.getPatientService().getPatient(7)));
		int before = observationsByPerson.size();
		String json = "{\"location\":\"dc5c1fcc-0459-4201-bf70-0b90535ba362\",\"concept\":\"a09ab2c5-878e-4905-b25d-5784167d0216\",\"person\":\"5946f880-b197-400b-9caa-a3c661d23041\",\"obsDatetime\":\"2011-05-18\",\"value\":\"150.0\"}";
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		Object newObs = deserialize(handle(req));
		
		List<Obs> observationsByPersonAfterSave = Context.getObsService().getObservationsByPerson(
		    (Context.getPatientService().getPatient(7)));
		Assert.assertEquals(before + 1, observationsByPersonAfterSave.size());
		newObs = observationsByPersonAfterSave.get(0);
		Assert.assertEquals((Double) 150.0, ((Obs) newObs).getValueNumeric());
	}
	
	/**
	 * @see ObsController#createObs(SimpleObject,WebRequest,HttpServletResponse)
	 * @verifies create a new obs with text concept
	 */
	@Test
	//@Ignore("RESTWS-238: Define creatable/updatable properties on Obs resource")
	public void createObs_shouldCreateANewObsWithTextConcept() throws Exception {
		List<Obs> observationsByPerson = Context.getObsService().getObservationsByPerson(
		    (Context.getPatientService().getPatient(7)));
		int before = observationsByPerson.size();
		String json = "{\"location\":\"dc5c1fcc-0459-4201-bf70-0b90535ba362\",\"concept\":\"96408258-000b-424e-af1a-403919332938\",\"person\":\"5946f880-b197-400b-9caa-a3c661d23041\",\"obsDatetime\":\"2011-05-18\",\"value\":\"high\"}";
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		Object newObs = deserialize(handle(req));
		
		List<Obs> observationsByPersonAfterSave = Context.getObsService().getObservationsByPerson(
		    (Context.getPatientService().getPatient(7)));
		Assert.assertEquals(before + 1, observationsByPersonAfterSave.size());
		newObs = observationsByPersonAfterSave.get(0);
		Assert.assertEquals("high", ((Obs) newObs).getValueText());
	}
	
	/**
	 * @verifies void a obs
	 */
	@Test
	public void voidObs_shouldVoidAObs() throws Exception {
		String uuid = "be48cdcb-6a76-47e3-9f2e-2635032f3a9a";
		
		Obs obs = Context.getObsService().getObs(9);
		Assert.assertFalse(obs.isVoided());
		handle(newDeleteRequest(getURI() + "/" + uuid, new Parameter("reason", "unit test")));
		obs = Context.getObsService().getObs(9);
		Assert.assertTrue(obs.isVoided());
		Assert.assertEquals("unit test", obs.getVoidReason());
	}
	
	/**
	 * @see ObsController#updatePatient(String,SimpleObject,WebRequest,HttpServletResponse)
	 * @verifies change a property on an obs
	 */
	@Test
	public void updateObs_shouldChangeAPropertyOnAnObs() throws Exception {
		String json = "{\"value\": \"35.0\"}";
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		
		Obs oldObs = Context.getObsService().getObsByUuid(getUuid());
		assertThat(oldObs.isVoided(), is(true));
		
		List<Obs> observations = Context.getObsService().getObservationsByPersonAndConcept(oldObs.getPerson(),
		    oldObs.getConcept());
		Obs obs = observations.get(observations.size() - 1);
		assertThat(obs.getValueNumeric(), is(35.0));
	}
	
	@Test
	public void updateObs_shouldChangeAComplexPropertyOnAnObs() throws Exception {
		String locationUuid = "9356400c-a5a2-4532-8f2b-2361b3446eb8";
		String json = "{\"location\":\"" + locationUuid + "\"}";
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		
		Obs oldObs = Context.getObsService().getObsByUuid(getUuid());
		assertThat(oldObs.isVoided(), is(true));
		
		List<Obs> observations = Context.getObsService().getObservationsByPersonAndConcept(oldObs.getPerson(),
		    oldObs.getConcept());
		Obs obs = observations.get(observations.size() - 1);
		assertThat(obs.getLocation().getUuid(), is(locationUuid));
	}
	
	/**
	 * @verifies fail to purge an obs with dependent data
	 */
	@Test
	@ExpectedException(APIException.class)
	public void purgeObs_shouldFailToPurgeAnObsWithDependentData() throws Exception {
		executeDataSet("org/openmrs/api/include/ObsServiceTest-complex.xml");
		handle(newDeleteRequest(getURI() + "/9b6639b2-5785-4603-a364-075c2d61cd51", new Parameter("purge", "")));
	}
	
	/**
	 * @see ObsController#purgeObs(String,WebRequest,HttpServletResponse)
	 * @verifies purge a simple obs
	 */
	@Test
	public void purgeObs_shouldPurgeASimpleObs() throws Exception {
		Context.getObsService().getObsByUuid(getUuid());
		Assert.assertNotNull(Context.getObsService().getObsByUuid(getUuid()));
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		req.addParameter("purge", "");
		handle(req);
		Assert.assertNull(Context.getObsService().getObsByUuid(getUuid()));
	}
	
	@Test
	public void searchByPatient_shouldGetObsForAPatient() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI(),
		    new Parameter("patient", "5946f880-b197-400b-9caa-a3c661d23041"));
		SimpleObject result = deserialize(handle(req));
		List<Object> results = Util.getResultsList(result);
		Assert.assertEquals(9, results.size());
	}
	
	@Test
	//@Ignore("RESTWS-238: Define creatable/updatable properties on Obs resource")
	public void createObs_shouldCreateAnObsWhenUnitsAreSpecifiedForAConceptNumeric() throws Exception {
		String conceptUuid = "c607c80f-1ea9-4da3-bb88-6276ce8868dd";
		List<Obs> observationsByPerson = Context.getObsService().getObservationsByPerson(
		    (Context.getPatientService().getPatient(7)));
		int before = observationsByPerson.size();
		String json = "{\"location\":\"dc5c1fcc-0459-4201-bf70-0b90535ba362\",\"concept\":\""
		        + conceptUuid
		        + "\",\"person\":\"5946f880-b197-400b-9caa-a3c661d23041\",\"obsDatetime\":\"2011-05-18\",\"value\":\"90.0 kg\"}";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		Object newObs = deserialize(handle(req));
		
		List<Obs> observationsByPersonAfterSave = Context.getObsService().getObservationsByPerson(
		    (Context.getPatientService().getPatient(7)));
		Assert.assertEquals(before + 1, observationsByPersonAfterSave.size());
		newObs = observationsByPersonAfterSave.get(0);
		Assert.assertEquals((Double) 90.0, ((Obs) newObs).getValueNumeric());
	}
	
	/**
	 * @see ObsResource1_8#create(SimpleObject, org.openmrs.module.webservices.rest.web.RequestContext)
	 * @throws Exception
	 */
	@Test(expected = ConversionException.class)
	public void createObs_shouldFailIfAnObsHasInvalidUnitsForAConceptNumeric() throws Exception {
		String json = "{\"location\":\"dc5c1fcc-0459-4201-bf70-0b90535ba362\",\"concept\":\"c607c80f-1ea9-4da3-bb88-6276ce8868dd\",\"person\":\"5946f880-b197-400b-9caa-a3c661d23041\",\"obsDatetime\":\"2011-05-18\",\"value\":\"90.0 KGs\"}";
		handle(newPostRequest(getURI(), json));
	}
	
	/**
	 * @see ObsController#getObs(String,WebRequest)
	 * @verifies get a dateTime obs value correctly represented as ISO8601 long format
	 */
	@Test
	public void getObs_shouldGetADateObs() throws Exception {
		String uuid = "99b92980-db62-40cd-8bca-733357c48126";
		SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + uuid);
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Util.log("Obs fetched (default)", result);
		Assert.assertEquals(uuid, PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals(ConversionUtil.convertToRepresentation(ymd.parse("2008-08-14"), Representation.DEFAULT),
		    PropertyUtils.getProperty(result, "value"));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "obs";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_8.OBS_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#shouldGetAll()
	 */
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	
	@Override
	public long getAllCount() {
		return 0; //Not supported
	}
	
}
