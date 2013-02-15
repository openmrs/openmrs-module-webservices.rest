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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Rest1_9TestConstants;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest;

public class VisitController1_9Test extends BaseCrudControllerTest {
	
	private VisitService service;
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "visit";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return Rest1_9TestConstants.VISIT_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return 0;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#shouldGetAll()
	 */
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
	
	@Before
	public void before() {
		this.service = Context.getVisitService();
	}
	
	@Test
	public void shouldCreateAVisit() throws Exception {
		int originalCount = service.getAllVisits().size();
		String json = "{ \"patient\":\"5946f880-b197-400b-9caa-a3c661d23041\", \"visitType\":\""
		        + Rest1_9TestConstants.VISIT_TYPE_UUID + "\", \"location\":\"" + Rest1_9TestConstants.LOCATION_UUID
		        + "\", \"startDatetime\":\"" + DATE_FORMAT.format(new Date()) + "\"}";
		
		Object newVisit = deserialize(handle(newPostRequest(getURI(), json)));
		
		Assert.assertNotNull(PropertyUtils.getProperty(newVisit, "uuid"));
		Assert.assertEquals(originalCount + 1, service.getAllVisits().size());
	}
	
	@Test
	public void shouldCreateAVisitWithEncounters() throws Exception {
		int originalCount = service.getAllVisits().size();
		final String patientUuid = "5946f880-b197-400b-9caa-a3c661d23041";
		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		Assert.assertEquals(0, service.getVisitsByPatient(patient).size());
		String json = "{ \"patient\":\"5946f880-b197-400b-9caa-a3c661d23041\", \"visitType\":\""
		        + Rest1_9TestConstants.VISIT_TYPE_UUID
		        + "\", \"location\":\""
		        + Rest1_9TestConstants.LOCATION_UUID
		        + "\", \"startDatetime\":\""
		        + DATE_FORMAT.format(new Date())
		        + "\", \"encounters\": [\"6519d653-393b-4118-9c83-a3715b82d4ac\", \"eec646cb-c847-45a7-98bc-91c8c4f70add\"] }";
		Object newVisit = deserialize(handle(newPostRequest(getURI(), json)));
		Assert.assertNotNull(PropertyUtils.getProperty(newVisit, "uuid"));
		Assert.assertEquals(originalCount + 1, service.getAllVisits().size());
		Assert.assertEquals(2, service.getVisitsByPatient(patient).get(0).getEncounters().size());
	}
	
	@Test
	public void shouldCreateAVisitWithAttributes() throws Exception {
		int originalCount = service.getAllVisits().size();
		String json = "{ \"patient\":\"5946f880-b197-400b-9caa-a3c661d23041\", \"visitType\":\""
		        + Rest1_9TestConstants.VISIT_TYPE_UUID + "\", \"location\":\"" + Rest1_9TestConstants.LOCATION_UUID
		        + "\", \"startDatetime\":\"" + DATE_FORMAT.format(new Date()) + "\","
		        + "\"attributes\":[{\"attributeType\":\"" + Rest1_9TestConstants.VISIT_ATTRIBUTE_TYPE_UUID
		        + "\",\"value\":\"2012-12-01\"}]}";
		Object newVisit = deserialize(handle(newPostRequest(getURI(), json)));
		Assert.assertNotNull(PropertyUtils.getProperty(newVisit, "uuid"));
		Assert.assertEquals(originalCount + 1, service.getAllVisits().size());
	}
	
	@Test
	public void shouldEditAVisit() throws Exception {
		final String newVisitTypeUuid = Rest1_9TestConstants.VISIT_TYPE_UUID;
		final String newLocationUuid = "9356400c-a5a2-4532-8f2b-2361b3446eb8";
		final String newIndicationConceptUuid = "c607c80f-1ea9-4da3-bb88-6276ce8868dd";
		final Date newStartDatetime = new Date();
		final Date newStopDatetime = new Date();
		Visit visit = service.getVisitByUuid(Rest1_9TestConstants.VISIT_UUID);
		Assert.assertNotNull(visit);
		//sanity checks
		Assert.assertFalse(newVisitTypeUuid.equalsIgnoreCase(visit.getVisitType().getUuid()));
		Assert.assertFalse(newLocationUuid.equalsIgnoreCase(visit.getLocation().getUuid()));
		Assert.assertFalse(newIndicationConceptUuid.equalsIgnoreCase(visit.getIndication().getUuid()));
		Assert.assertFalse(newStartDatetime.equals(visit.getStartDatetime()));
		Assert.assertFalse(newStopDatetime.equals(visit.getStopDatetime()));
		
		String json = "{ \"visitType\":\"" + newVisitTypeUuid + "\", \"location\":\"" + newLocationUuid
		        + "\", \"indication\":\"" + newIndicationConceptUuid + "\", \"startDatetime\":\""
		        + DATE_FORMAT.format(newStartDatetime) + "\", \"stopDatetime\":\"" + DATE_FORMAT.format(newStopDatetime)
		        + "\" }";
		
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		
		Visit updated = service.getVisitByUuid(Rest1_9TestConstants.VISIT_UUID);
		Assert.assertNotNull(updated);
		Assert.assertEquals(newVisitTypeUuid, updated.getVisitType().getUuid());
		Assert.assertEquals(newLocationUuid, updated.getLocation().getUuid());
		Assert.assertEquals(newIndicationConceptUuid, updated.getIndication().getUuid());
		Assert.assertEquals(newStartDatetime, updated.getStartDatetime());
		Assert.assertEquals(newStopDatetime, updated.getStopDatetime());
	}
	
	@Test
	public void shouldAddEncountersToAnExistingVisitOnEdit() throws Exception {
		Visit visit = service.getVisitByUuid(Rest1_9TestConstants.VISIT_UUID);
		Assert.assertEquals(0, visit.getEncounters().size());
		
		String json = "{\"encounters\": [\"6519d653-393b-4118-9c83-a3715b82d4ac\", \"eec646cb-c847-45a7-98bc-91c8c4f70add\"] }";
		
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		
		Visit updated = service.getVisitByUuid(Rest1_9TestConstants.VISIT_UUID);
		Assert.assertEquals(2, updated.getEncounters().size());
	}
	
	@Test
	public void shouldRemoveAnEncounterFromAnExistingVisitOnEdit() throws Exception {
		final String encounterId = "6519d653-393b-4118-9c83-a3715b82d4ac";
		Visit visit = service.getVisitByUuid(Rest1_9TestConstants.VISIT_UUID);
		//add an encounter to be removed for testing purposes
		visit.getEncounters().add(Context.getEncounterService().getEncounterByUuid(encounterId));
		service.saveVisit(visit);
		Assert.assertEquals(1, visit.getEncounters().size());
		
		String json = "{\"encounters\": [] }";
		
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		
		Visit updated = service.getVisitByUuid(Rest1_9TestConstants.VISIT_UUID);
		Assert.assertEquals(0, updated.getEncounters().size());
	}
	
	@Test
	public void shouldVoidAVisit() throws Exception {
		Visit visit = service.getVisitByUuid(Rest1_9TestConstants.VISIT_UUID);
		Assert.assertFalse(visit.isVoided());
		
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("reason", "test reason")));
		
		visit = service.getVisitByUuid(Rest1_9TestConstants.VISIT_UUID);
		Assert.assertTrue(visit.isVoided());
		Assert.assertEquals("test reason", visit.getVoidReason());
	}
	
	@Test
	public void shouldPurgeAVisit() throws Exception {
		Assert.assertNotNull(service.getVisitByUuid(Rest1_9TestConstants.VISIT_UUID));
		int originalCount = service.getAllVisits().size();
		
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("purge", "")));
		
		Assert.assertNull(service.getVisitByUuid(Rest1_9TestConstants.VISIT_UUID));
		Assert.assertEquals(originalCount - 1, service.getAllVisits().size());
	}
	
	/**
	 * @see {@link VisitController#searchByPatient(String,HttpServletRequest,HttpServletResponse)}
	 */
	@Test
	@Ignore("Depends on RESTWS-320")
	public void searchByPatient_shouldGetUnretiredVisitsForThePatient() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("patient",
		        "da7f524f-27ce-4bb2-86d6-6d1d05312bd5"))));
		
		Assert.assertEquals(3, Util.getResultsSize(result));
	}
	
}
