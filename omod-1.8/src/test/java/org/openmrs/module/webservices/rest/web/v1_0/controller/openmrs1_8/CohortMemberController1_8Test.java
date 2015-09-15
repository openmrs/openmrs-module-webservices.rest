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

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.CohortService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests functionality of {@link CohortMemberController}.
 */
public class CohortMemberController1_8Test extends MainResourceControllerTest {
	
	private static final String patientUuid = "a7e04421-525f-442f-8138-05b619d16def";
	
	private static final String datasetFilename = "customTestDataset.xml";
	
	private CohortService service;
	
	private PatientService patientService;
	
	@Before
	public void before() throws Exception {
		this.service = Context.getCohortService();
		this.patientService = Context.getPatientService();
		executeDataSet(datasetFilename);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "cohort";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return 2;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_8.COHORT_UUID;
	}
	
	@Test
	public void getCohortMember_shouldGetADefaultRepresentationOfACohortMember() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid() + "/member/" + patientUuid);
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Util.log("Cohort member fetched (default)", result);
	}
	
	@Test
	public void getAllCohortMembers_shouldGetARefRepresentationOfAllCohortMembers() throws Exception {
		int size = service.getCohortByUuid(getUuid()).getMemberIds().size();
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid() + "/member");
		SimpleObject result = deserialize(handle(req));
		
		Util.log("Cohort member fetched (ref)", result);
		Assert.assertEquals(size, Util.getResultsSize(result));
	}
	
	@Test
	public void getAllCohortMembers_shouldGetADefaultRepresentationOfAllCohortMembers() throws Exception {
		int size = service.getCohortByUuid(getUuid()).getMemberIds().size();
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid() + "/member");
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));
		
		Util.log("Cohort member fetched (default)", result);
		Assert.assertEquals(size, Util.getResultsSize(result));
	}
	
	@Test
	public void addCohortMember_shouldAddCohortMember() throws Exception {
		
		String patientId = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
		
		SimpleObject attributes = new SimpleObject();
		attributes.add("patient", patientId);
		String json = new ObjectMapper().writeValueAsString(attributes);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/" + getUuid() + "/member");
		req.setContent(json.getBytes());
		handle(req);
		
		Cohort cohort = service.getCohortByUuid(getUuid());
		Patient patient = patientService.getPatientByUuid(patientId);
		Assert.assertTrue(cohort.contains(patient));
	}
	
	@Test
	public void removeCohortMember_shouldRemoveCohortMember() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid() + "/member/" + patientUuid);
		req.addParameter("!purge", "");
		handle(req);
		
		Cohort cohort = service.getCohortByUuid(getUuid());
		Patient patient = patientService.getPatientByUuid(patientUuid);
		Assert.assertTrue(!cohort.contains(patient));
		
	}
	
}
