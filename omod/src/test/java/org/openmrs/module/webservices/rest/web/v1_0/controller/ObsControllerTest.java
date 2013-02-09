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

import org.openmrs.module.webservices.rest.web.v1_0.controller.ObsController;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.v1_0.resource.ObsResource;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.web.context.request.WebRequest;

public class ObsControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	private MockHttpServletRequest emptyRequest() {
	//		return new MockHttpServletRequest();
	//	}
	//	
	//	/**
	//	 * @see ObsController#getObs(String,WebRequest)
	//	 * @verifies get a default representation of a obs
	//	 */
	//	
	//	@Test
	//	public void getObs_shouldGetADefaultRepresentationOfAObs() throws Exception {
	//		Object result = new ObsController().retrieve("39fb7f47-e80a-4056-9285-bd798be13c63", emptyRequest());
	//		Assert.assertNotNull(result);
	//		Util.log("Obs fetched (default)", result);
	//		Assert.assertEquals("39fb7f47-e80a-4056-9285-bd798be13c63", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "concept"));
	//	}
	//	
	//	/**
	//	 * @see ObsController#getObs(String,WebRequest)
	//	 * @verifies get a full representation of a obs
	//	 */
	//	@Test
	//	public void getObs_shouldGetAFullRepresentationOfAObs() throws Exception {
	//		MockHttpServletRequest req = new MockHttpServletRequest();
	//		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//		Object result = new ObsController().retrieve("39fb7f47-e80a-4056-9285-bd798be13c63", req);
	//		Assert.assertNotNull(result);
	//		Util.log("Obs fetched (default)", result);
	//		Assert.assertEquals("39fb7f47-e80a-4056-9285-bd798be13c63", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "concept"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	/**
	//	 * @see ObsController#getObsByPatientId(String,WebRequest)
	//	 * @verifies get a default representation of all obs
	//	 */
	//	@Test
	//	public void getObsByPatientId_shouldGetADefaultRepresentationOfAllObs() throws Exception {
	//		List<Object> results = (List<Object>) new ObsController().search("6TS-4", emptyRequest(),
	//		    new MockHttpServletResponse()).get("results");
	//		Assert.assertNotNull(results);
	//		Object result = results.get(8);
	//		Assert.assertEquals(9, results.size());
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
	//	}
	//	
	//	@Test
	//	public void searchByEncounter_shouldGetObsInAnEncounter() throws Exception {
	//		SimpleObject search = new ObsController().searchByEncounter("6519d653-393b-4118-9c83-a3715b82d4ac", emptyRequest(),
	//		    new MockHttpServletResponse());
	//		List<Object> results = (List<Object>) search.get("results");
	//		Assert.assertEquals(2, results.size());
	//		List<Object> uuids = Arrays.asList(PropertyUtils.getProperty(results.get(0), "uuid"), PropertyUtils.getProperty(
	//		    results.get(1), "uuid"));
	//		Assert.assertTrue(uuids.contains("39fb7f47-e80a-4056-9285-bd798be13c63"));
	//		Assert.assertTrue(uuids.contains("be48cdcb-6a76-47e3-9f2e-2635032f3a9a"));
	//	}
	//	
	//	/**
	//	 * @see ObsController#createObs(SimpleObject,WebRequest,HttpServletResponse)
	//	 * @verifies create a new obs with numeric concept
	//	 */
	//	@Test
	//	@Ignore("RESTWS-238: Define creatable/updatable properties on Obs resource")
	//	public void createObs_shouldCreateANewObsWithNumericConcept() throws Exception {
	//		List<Obs> observationsByPerson = Context.getObsService().getObservationsByPerson(
	//		    (Context.getPatientService().getPatient(7)));
	//		int before = observationsByPerson.size();
	//		String json = "{\"location\":\"dc5c1fcc-0459-4201-bf70-0b90535ba362\",\"concept\":\"a09ab2c5-878e-4905-b25d-5784167d0216\",\"person\":\"5946f880-b197-400b-9caa-a3c661d23041\",\"obsDatetime\":\"2011-05-18\",\"value\":\"150.0\"}";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		new ObsController().create(post, emptyRequest(), new MockHttpServletResponse());
	//		List<Obs> observationsByPersonAfterSave = Context.getObsService().getObservationsByPerson(
	//		    (Context.getPatientService().getPatient(7)));
	//		Assert.assertEquals(before + 1, observationsByPersonAfterSave.size());
	//		Obs newObs = observationsByPersonAfterSave.get(0);
	//		Assert.assertEquals(150.0, newObs.getValueNumeric());
	//	}
	//	
	//	/**
	//	 * @see ObsController#createObs(SimpleObject,WebRequest,HttpServletResponse)
	//	 * @verifies create a new obs with text concept
	//	 */
	//	@Test
	//	@Ignore("RESTWS-238: Define creatable/updatable properties on Obs resource")
	//	public void createObs_shouldCreateANewObsWithTextConcept() throws Exception {
	//		List<Obs> observationsByPerson = Context.getObsService().getObservationsByPerson(
	//		    (Context.getPatientService().getPatient(7)));
	//		int before = observationsByPerson.size();
	//		String json = "{\"location\":\"dc5c1fcc-0459-4201-bf70-0b90535ba362\",\"concept\":\"96408258-000b-424e-af1a-403919332938\",\"person\":\"5946f880-b197-400b-9caa-a3c661d23041\",\"obsDatetime\":\"2011-05-18\",\"value\":\"high\"}";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		new ObsController().create(post, emptyRequest(), new MockHttpServletResponse());
	//		List<Obs> observationsByPersonAfterSave = Context.getObsService().getObservationsByPerson(
	//		    (Context.getPatientService().getPatient(7)));
	//		Assert.assertEquals(before + 1, observationsByPersonAfterSave.size());
	//		Obs newObs = observationsByPersonAfterSave.get(0);
	//		Assert.assertEquals("high", newObs.getValueText());
	//	}
	//	
	//	/**
	//	 * @see ObsController#voidObs(String,String,WebRequest,HttpServletResponse)
	//	 * @verifies void a obs
	//	 */
	//	@Test
	//	public void voidObs_shouldVoidAObs() throws Exception {
	//		Obs obs = Context.getObsService().getObs(9);
	//		Assert.assertFalse(obs.isVoided());
	//		new ObsController().delete("be48cdcb-6a76-47e3-9f2e-2635032f3a9a", "unit test", emptyRequest(),
	//		    new MockHttpServletResponse());
	//		obs = Context.getObsService().getObs(9);
	//		Assert.assertTrue(obs.isVoided());
	//		Assert.assertEquals("unit test", obs.getVoidReason());
	//	}
	//	
	//	/**
	//	 * @see ObsController#updatePatient(String,SimpleObject,WebRequest,HttpServletResponse)
	//	 * @verifies change a property on an obs
	//	 */
	//	@Test
	//	@Ignore("RESTWS-238: Define creatable/updatable properties on Obs resource")
	//	public void updateObs_shouldChangeAPropertyOnAnObs() throws Exception {
	//		SimpleObject post = new ObjectMapper().readValue("{\"valueNumeric\": 35.0}", SimpleObject.class);
	//		Object editedObs = new ObsController().update("39fb7f47-e80a-4056-9285-bd798be13c63", post, emptyRequest(),
	//		    new MockHttpServletResponse());
	//		List<Obs> obsList = Context.getObsService().getObservationsByPerson(Context.getPersonService().getPerson(7));
	//		Obs newObs = obsList.get(obsList.size() - 1);
	//		Obs oldObs = Context.getObsService().getObsByUuid("39fb7f47-e80a-4056-9285-bd798be13c63");
	//		Assert.assertTrue(Context.getObsService().getObsByUuid("39fb7f47-e80a-4056-9285-bd798be13c63").isVoided());
	//		Assert.assertFalse(oldObs.getValueNumeric().equals(new Double("35.0")));
	//		Assert.assertTrue(newObs.getValueNumeric().equals(new Double("35.0")));
	//	}
	//	
	//	/**
	//	 * @see ObsController#updatePatient(String,SimpleObject,WebRequest,HttpServletResponse)
	//	 * @verifies change a complex property on an obs
	//	 */
	//	@Test
	//	@Ignore("RESTWS-238: Define creatable/updatable properties on Obs resource")
	//	public void updateObs_shouldChangeAComplexPropertyOnAnObs() throws Exception {
	//		
	//		String json = "{\"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\"}";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object editedObs = new ObsController().update("39fb7f47-e80a-4056-9285-bd798be13c63", post, emptyRequest(),
	//		    new MockHttpServletResponse());
	//		Obs oldObs = Context.getObsService().getObsByUuid("39fb7f47-e80a-4056-9285-bd798be13c63");
	//		List<Obs> obsList = Context.getObsService().getObservationsByPerson(Context.getPersonService().getPerson(7));
	//		Obs newObs = obsList.get(obsList.size() - 1);
	//		Assert.assertTrue(Context.getObsService().getObsByUuid("39fb7f47-e80a-4056-9285-bd798be13c63").isVoided());
	//		Assert.assertFalse(new Integer(2).equals(oldObs.getLocation().getId()));
	//		Assert.assertTrue(new Integer(2).equals(newObs.getLocation().getId()));
	//	}
	//	
	//	/**
	//	 * @see ObsController#purgeObs(String,WebRequest,HttpServletResponse)
	//	 * @verifies fail to purge an obs with dependent data
	//	 */
	//	@Test
	//	@ExpectedException(APIException.class)
	//	public void purgeObs_shouldFailToPurgeAnObsWithDependentData() throws Exception {
	//		executeDataSet("org/openmrs/api/include/ObsServiceTest-complex.xml");
	//		new ObsController().purge("9b6639b2-5785-4603-a364-075c2d61cd51", emptyRequest(), new MockHttpServletResponse());
	//	}
	//	
	//	/**
	//	 * @see ObsController#purgeObs(String,WebRequest,HttpServletResponse)
	//	 * @verifies purge a simple obs
	//	 */
	//	@Test
	//	public void purgeObs_shouldPurgeASimpleObs() throws Exception {
	//		Assert.assertNotNull(Context.getObsService().getObsByUuid("39fb7f47-e80a-4056-9285-bd798be13c63"));
	//		new ObsController().purge("39fb7f47-e80a-4056-9285-bd798be13c63", emptyRequest(), new MockHttpServletResponse());
	//		Assert.assertNull(Context.getObsService().getObsByUuid("39fb7f47-e80a-4056-9285-bd798be13c63"));
	//	}
	//	
	//	/**
	//	 * @see ObsResource#getObsByPatient(String,
	//	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	//	 * @throws Exception
	//	 */
	//	@SuppressWarnings("unchecked")
	//	@Test
	//	public void searchByPatient_shouldGetObsForAPatient() throws Exception {
	//		executeDataSet("org/openmrs/api/include/ObsServiceTest-initial.xml");
	//		SimpleObject search = new ObsController().searchByPatient("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", emptyRequest(),
	//		    new MockHttpServletResponse());
	//		List<Object> results = (List<Object>) search.get("results");
	//		Assert.assertEquals(3, results.size());
	//		List<Object> uuids = Arrays.asList(PropertyUtils.getProperty(results.get(0), "uuid"), PropertyUtils.getProperty(
	//		    results.get(1), "uuid"), PropertyUtils.getProperty(results.get(2), "uuid"));
	//		Assert.assertTrue(uuids.contains("be3a4d7a-f9ab-47bb-aaad-bc0b452fcda4"));
	//		Assert.assertTrue(uuids.contains("b5499df2-b17c-4b39-88a6-44591c165569"));
	//		Assert.assertTrue(uuids.contains("0ee1248e-08aa-4a2c-9f38-fb3875f605e3"));
	//	}
	//	
	//	/**
	//	 * @see ObsResource#create(SimpleObject, org.openmrs.module.webservices.rest.web.RequestContext)
	//	 * @throws Exception
	//	 */
	//	@Test
	//	@Ignore("RESTWS-238: Define creatable/updatable properties on Obs resource")
	//	public void createObs_shouldCreateAnObsWhenUnitsAreSpecifiedForAConceptNumeric() throws Exception {
	//		String conceptUuid = "c607c80f-1ea9-4da3-bb88-6276ce8868dd";
	//		List<Obs> observationsByPerson = Context.getObsService().getObservationsByPerson(
	//		    (Context.getPatientService().getPatient(7)));
	//		int before = observationsByPerson.size();
	//		String json = "{\"location\":\"dc5c1fcc-0459-4201-bf70-0b90535ba362\",\"concept\":\""
	//		        + conceptUuid
	//		        + "\",\"person\":\"5946f880-b197-400b-9caa-a3c661d23041\",\"obsDatetime\":\"2011-05-18\",\"value\":\"90.0 kg\"}";
	//		
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		new ObsController().create(post, emptyRequest(), new MockHttpServletResponse());
	//		List<Obs> observationsByPersonAfterSave = Context.getObsService().getObservationsByPerson(
	//		    (Context.getPatientService().getPatient(7)));
	//		Assert.assertEquals(before + 1, observationsByPersonAfterSave.size());
	//		Obs newObs = observationsByPersonAfterSave.get(0);
	//		Assert.assertEquals(90.0, newObs.getValueNumeric());
	//	}
	//	
	//	/**
	//	 * @see ObsResource#create(SimpleObject, org.openmrs.module.webservices.rest.web.RequestContext)
	//	 * @throws Exception
	//	 */
	//	@Test(expected = ConversionException.class)
	//	@Ignore("RESTWS-238: Define creatable/updatable properties on Obs resource")
	//	public void createObs_shouldFailIfAnObsHasInvalidUnitsForAConceptNumeric() throws Exception {
	//		String json = "{\"location\":\"dc5c1fcc-0459-4201-bf70-0b90535ba362\",\"concept\":\"c607c80f-1ea9-4da3-bb88-6276ce8868dd\",\"person\":\"5946f880-b197-400b-9caa-a3c661d23041\",\"obsDatetime\":\"2011-05-18\",\"value\":\"90.0 KGs\"}";
	//		
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		new ObsController().create(post, emptyRequest(), new MockHttpServletResponse());
	//	}
	//	
	//	/**
	//	 * @see ObsController#getObs(String,WebRequest)
	//	 * @verifies get a dateTime obs value correctly represented as ISO8601 long format
	//	 */
	//	@Test
	//	public void getObs_shouldGetADateObs() throws Exception {
	//		SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
	//		Object result = new ObsController().retrieve("99b92980-db62-40cd-8bca-733357c48126", emptyRequest());
	//		Assert.assertNotNull(result);
	//		Util.log("DateTime Obs fetched (default)", result);
	//		Assert.assertEquals("99b92980-db62-40cd-8bca-733357c48126", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertEquals(ConversionUtil.convertToRepresentation(ymd.parse("2008-08-14"), Representation.DEFAULT),
	//		    PropertyUtils.getProperty(result, "value"));
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
