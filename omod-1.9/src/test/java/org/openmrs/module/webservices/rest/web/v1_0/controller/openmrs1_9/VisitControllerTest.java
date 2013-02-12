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

import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

public class VisitControllerTest extends BaseModuleWebContextSensitiveTest {
	
	@Test
	public void fakeTest() {
		
	}
	
//	private VisitService service;
//	
//	private VisitController controller;
//	
//	private MockHttpServletRequest request;
//	
//	private HttpServletResponse response;
//	
//	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
//	
//	@Before
//	public void before() {
//		this.service = Context.getVisitService();
//		this.controller = new VisitController();
//		this.request = new MockHttpServletRequest();
//		this.response = new MockHttpServletResponse();
//	}
//	
//	@Test
//	public void shouldGetAVisitByUuid() throws Exception {
//		Object result = controller.retrieve(Rest19ExtTestConstants.VISIT_UUID, request);
//		Assert.assertNotNull(result);
//		Assert.assertEquals(Rest19ExtTestConstants.VISIT_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "visitType"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "patient"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "location"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "indication"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "startDatetime"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "attributes"));
//		Assert.assertNull(PropertyUtils.getProperty(result, "stopDatetime"));
//		Assert.assertNull(PropertyUtils.getProperty(result, "auditinfo"));
//		Util.log("VISIT:", result);
//	}
//	
//	@Test
//	public void shouldCreateAVisit() throws Exception {
//		int originalCount = service.getAllVisits().size();
//		String json = "{ \"patient\":\"5946f880-b197-400b-9caa-a3c661d23041\", \"visitType\":\""
//		        + Rest19ExtTestConstants.VISIT_TYPE_UUID + "\", \"location\":\"" + Rest19ExtTestConstants.LOCATION_UUID
//		        + "\", \"startDatetime\":\"" + DATE_FORMAT.format(new Date()) + "\"}";
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		Object newVisit = controller.create(post, request, response);
//		Assert.assertNotNull(PropertyUtils.getProperty(newVisit, "uuid"));
//		Assert.assertEquals(originalCount + 1, service.getAllVisits().size());
//	}
//	
//	@Test
//	public void shouldCreateAVisitWithEncounters() throws Exception {
//		int originalCount = service.getAllVisits().size();
//		final String patientUuid = "5946f880-b197-400b-9caa-a3c661d23041";
//		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
//		Assert.assertEquals(0, service.getVisitsByPatient(patient).size());
//		String json = "{ \"patient\":\"5946f880-b197-400b-9caa-a3c661d23041\", \"visitType\":\""
//		        + Rest19ExtTestConstants.VISIT_TYPE_UUID
//		        + "\", \"location\":\""
//		        + Rest19ExtTestConstants.LOCATION_UUID
//		        + "\", \"startDatetime\":\""
//		        + DATE_FORMAT.format(new Date())
//		        + "\", \"encounters\": [\"6519d653-393b-4118-9c83-a3715b82d4ac\", \"eec646cb-c847-45a7-98bc-91c8c4f70add\"] }";
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		Object newVisit = controller.create(post, request, response);
//		Assert.assertNotNull(PropertyUtils.getProperty(newVisit, "uuid"));
//		Assert.assertEquals(originalCount + 1, service.getAllVisits().size());
//		Assert.assertEquals(2, service.getVisitsByPatient(patient).get(0).getEncounters().size());
//	}
//	
//	@Test
//	public void shouldCreateAVisitWithAttributes() throws Exception {
//		int originalCount = service.getAllVisits().size();
//		String json = "{ \"patient\":\"5946f880-b197-400b-9caa-a3c661d23041\", \"visitType\":\""
//		        + Rest19ExtTestConstants.VISIT_TYPE_UUID + "\", \"location\":\"" + Rest19ExtTestConstants.LOCATION_UUID
//		        + "\", \"startDatetime\":\"" + DATE_FORMAT.format(new Date()) + "\","
//		        + "\"attributes\":[{\"attributeType\":\"" + Rest19ExtTestConstants.VISIT_ATTRIBUTE_TYPE_UUID
//		        + "\",\"value\":\"2012-12-01\"}]}";
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		Object newVisit = controller.create(post, request, response);
//		Assert.assertNotNull(PropertyUtils.getProperty(newVisit, "uuid"));
//		Assert.assertEquals(originalCount + 1, service.getAllVisits().size());
//	}
//	
//	@Test
//	public void shouldEditAVisit() throws Exception {
//		final String newVisitTypeUuid = Rest19ExtTestConstants.VISIT_TYPE_UUID;
//		final String newLocationUuid = "9356400c-a5a2-4532-8f2b-2361b3446eb8";
//		final String newIndicationConceptUuid = "c607c80f-1ea9-4da3-bb88-6276ce8868dd";
//		final Date newStartDatetime = new Date();
//		final Date newStopDatetime = new Date();
//		Visit visit = service.getVisitByUuid(Rest19ExtTestConstants.VISIT_UUID);
//		Assert.assertNotNull(visit);
//		//sanity checks
//		Assert.assertFalse(newVisitTypeUuid.equalsIgnoreCase(visit.getVisitType().getUuid()));
//		Assert.assertFalse(newLocationUuid.equalsIgnoreCase(visit.getLocation().getUuid()));
//		Assert.assertFalse(newIndicationConceptUuid.equalsIgnoreCase(visit.getIndication().getUuid()));
//		Assert.assertFalse(newStartDatetime.equals(visit.getStartDatetime()));
//		Assert.assertFalse(newStopDatetime.equals(visit.getStopDatetime()));
//		
//		String json = "{ \"visitType\":\"" + newVisitTypeUuid + "\", \"location\":\"" + newLocationUuid
//		        + "\", \"indication\":\"" + newIndicationConceptUuid + "\", \"startDatetime\":\""
//		        + DATE_FORMAT.format(newStartDatetime) + "\", \"stopDatetime\":\"" + DATE_FORMAT.format(newStopDatetime)
//		        + "\" }";
//		
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		controller.update(Rest19ExtTestConstants.VISIT_UUID, post, request, response);
//		
//		Visit updated = service.getVisitByUuid(Rest19ExtTestConstants.VISIT_UUID);
//		Assert.assertNotNull(updated);
//		Assert.assertEquals(newVisitTypeUuid, updated.getVisitType().getUuid());
//		Assert.assertEquals(newLocationUuid, updated.getLocation().getUuid());
//		Assert.assertEquals(newIndicationConceptUuid, updated.getIndication().getUuid());
//		Assert.assertEquals(newStartDatetime, updated.getStartDatetime());
//		Assert.assertEquals(newStopDatetime, updated.getStopDatetime());
//	}
//	
//	@Test
//	public void shouldAddEncountersToAnExistingVisitOnEdit() throws Exception {
//		Visit visit = service.getVisitByUuid(Rest19ExtTestConstants.VISIT_UUID);
//		Assert.assertEquals(0, visit.getEncounters().size());
//		
//		String json = "{\"encounters\": [\"6519d653-393b-4118-9c83-a3715b82d4ac\", \"eec646cb-c847-45a7-98bc-91c8c4f70add\"] }";
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		controller.update(Rest19ExtTestConstants.VISIT_UUID, post, request, response);
//		Visit updated = service.getVisitByUuid(Rest19ExtTestConstants.VISIT_UUID);
//		Assert.assertEquals(2, updated.getEncounters().size());
//	}
//	
//	@Test
//	public void shouldRemoveAnEncounterFromAnExistingVisitOnEdit() throws Exception {
//		final String encounterId = "6519d653-393b-4118-9c83-a3715b82d4ac";
//		Visit visit = service.getVisitByUuid(Rest19ExtTestConstants.VISIT_UUID);
//		//add an encounter to be removed for testing purposes
//		visit.getEncounters().add(Context.getEncounterService().getEncounterByUuid(encounterId));
//		service.saveVisit(visit);
//		Assert.assertEquals(1, visit.getEncounters().size());
//		
//		String json = "{\"encounters\": [] }";
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		controller.update(Rest19ExtTestConstants.VISIT_UUID, post, request, response);
//		Visit updated = service.getVisitByUuid(Rest19ExtTestConstants.VISIT_UUID);
//		Assert.assertEquals(0, updated.getEncounters().size());
//	}
//	
//	@Test
//	public void shouldVoidAVisit() throws Exception {
//		Visit visit = service.getVisitByUuid(Rest19ExtTestConstants.VISIT_UUID);
//		Assert.assertFalse(visit.isVoided());
//		
//		controller.delete(Rest19ExtTestConstants.VISIT_UUID, "test reason", request, response);
//		visit = service.getVisitByUuid(Rest19ExtTestConstants.VISIT_UUID);
//		Assert.assertTrue(visit.isVoided());
//		Assert.assertEquals("test reason", visit.getVoidReason());
//	}
//	
//	@Test
//	public void shouldPurgeAVisit() throws Exception {
//		Assert.assertNotNull(service.getVisitByUuid(Rest19ExtTestConstants.VISIT_UUID));
//		int originalCount = service.getAllVisits().size();
//		controller.purge(Rest19ExtTestConstants.VISIT_UUID, request, response);
//		Assert.assertNull(service.getVisitByUuid(Rest19ExtTestConstants.VISIT_UUID));
//		Assert.assertEquals(originalCount - 1, service.getAllVisits().size());
//	}
//	
//	@Test
//	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
//		request.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
//		Object result = controller.retrieve(Rest19ExtTestConstants.VISIT_UUID, request);
//		Assert.assertNotNull(result);
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
//	}
//	
//	/**
//	 * @see {@link VisitController#searchByPatient(String,HttpServletRequest,HttpServletResponse)}
//	 */
//	@SuppressWarnings("unchecked")
//	@Test
//	@Verifies(value = "should get visits for the patient", method = "searchByPatient(String,HttpServletRequest,HttpServletResponse)")
//	public void searchByPatient_shouldGetUnretiredVisitsForThePatient() throws Exception {
//		Assert.assertEquals(3, ((List<Object>) controller.searchByPatient("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", request,
//		    response).get("results")).size());
//	}
}
