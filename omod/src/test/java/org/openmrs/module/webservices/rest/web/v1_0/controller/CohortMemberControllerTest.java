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

import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

/**
 * Tests functionality of {@link CohortMemberController}.
 */
public class CohortMemberControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	private static final String cohortUuid = "05e08b3b-5690-41e1-b651-5391fd946c1a";
	//	
	//	private static final String patientUuid = "a7e04421-525f-442f-8138-05b619d16def";
	//	
	//	private static final String datasetFilename = "customTestDataset.xml";
	//	
	//	private CohortService service;
	//	
	//	private CohortMemberController controller;
	//	
	//	private MockHttpServletRequest request;
	//	
	//	private HttpServletResponse response;
	//	
	//	@Before
	//	public void before() throws Exception {
	//		this.service = Context.getCohortService();
	//		this.controller = new CohortMemberController();
	//		this.request = new MockHttpServletRequest();
	//		this.response = new MockHttpServletResponse();
	//		executeDataSet(datasetFilename);
	//	}
	//	
	//	@Test
	//	public void getCohortMember_shouldGetADefaultRepresentationOfACohortMember() throws Exception {
	//		Object result = controller.retrieve(cohortUuid, patientUuid, request);
	//		Assert.assertNotNull(result);
	//		Util.log("Cohort member fetched (default)", result);
	//	}
	//	
	//	@Test
	//	public void getAllCohortMembers_shouldGetARefRepresentationOfAllCohortMembers() throws Exception {
	//		int size = service.getCohortByUuid(cohortUuid).getMemberIds().size();
	//		SimpleObject result = controller.getAll(cohortUuid, request, response);
	//		Assert.assertNotNull(result);
	//		Util.log("Cohort member fetched (ref)", result);
	//		Assert.assertEquals(size, Util.getResultsSize(result));
	//	}
	//	
	//	@Test
	//	public void getAllCohortMembers_shouldGetADefaultRepresentationOfAllCohortMembers() throws Exception {
	//		int size = service.getCohortByUuid(cohortUuid).getMemberIds().size();
	//		MockHttpServletRequest req = new MockHttpServletRequest();
	//		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//		SimpleObject result = controller.getAll(cohortUuid, req, response);
	//		Assert.assertNotNull(result);
	//		Util.log("Cohort member fetched (default)", result);
	//		Assert.assertEquals(Util.getResultsSize(result), size);
	//	}
	//	
	//	@Test
	//	public void addCohortMember_shouldAddCohortMember() throws Exception {
	//		int before = service.getCohortByUuid(cohortUuid).getMemberIds().size();
	//		String json = "{ \"patient\":\"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object result = controller.create(cohortUuid, post, request, response);
	//		Util.log("Add patient to cohort : ", result);
	//		Assert.assertEquals(before + 1, service.getCohortByUuid(cohortUuid).getMemberIds().size());
	//	}
	//	
	//	@Test
	//	public void removeCohortMember_shouldRemoveCohortMember() throws Exception {
	//		int before = service.getCohortByUuid(cohortUuid).getMemberIds().size();
	//		Object result = controller.delete(cohortUuid, patientUuid, "because", request, response);
	//		Util.log("Removed patient from cohort : ", result);
	//		Assert.assertEquals(before - 1, service.getCohortByUuid(cohortUuid).getMemberIds().size());
	//	}
	
	@Test
	public void fakeTest() {
		
	}
	
}
