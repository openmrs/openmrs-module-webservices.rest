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
 * Tests functionality of {@link CohortController}.
 */
public class CohortControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	private static final String cohortUuid = "05e08b3b-5690-41e1-b651-5391fd946c1a";
	//	
	//	private static final String cohortName = "B13 deficit";
	//	
	//	private static final String cohortQuery = "B13";
	//	
	//	private static final String datasetFilename = "customTestDataset.xml";
	//	
	//	private CohortService service;
	//	
	//	private CohortController controller;
	//	
	//	private MockHttpServletRequest request;
	//	
	//	private HttpServletResponse response;
	//	
	//	@Before
	//	public void before() throws Exception {
	//		this.service = Context.getCohortService();
	//		this.controller = new CohortController();
	//		this.request = new MockHttpServletRequest();
	//		this.response = new MockHttpServletResponse();
	//		executeDataSet(datasetFilename);
	//	}
	//	
	//	@Test
	//	public void createCohort_shouldCreateANewCohort() throws Exception {
	//		int before = service.getAllCohorts().size();
	//		String json = "{ \"name\":\"NEW COHORT\", \"description\":\"THIS IS NEW COHORT\", \"memberIds\": [ 2, 6 ]}";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object newCohort = controller.create(post, request, response);
	//		Util.log("Created cohort", newCohort);
	//		Assert.assertEquals(before + 1, service.getAllCohorts().size());
	//	}
	//	
	//	@Test
	//	public void getCohort_shouldGetADefaultRepresentationOfACohort() throws Exception {
	//		Object result = controller.retrieve(cohortUuid, request);
	//		Assert.assertNotNull(result);
	//		Util.log("Cohort fetched (default)", result);
	//		Assert.assertEquals(cohortUuid, PropertyUtils.getProperty(result, "uuid"));
	//	}
	//	
	//	@Test
	//	public void getCohortByExactName_shouldGetADefaultRepresentationOfACohort() throws Exception {
	//		Object result = controller.retrieve(cohortName, request);
	//		Assert.assertNotNull(result);
	//		Util.log("Cohort fetched (default)", result);
	//		Assert.assertEquals(cohortName, PropertyUtils.getProperty(result, "name"));
	//	}
	//	
	//	@Test
	//	public void getCohorts_shouldSearchForCohortsByName() throws Exception {
	//		List<Object> results = (List<Object>) controller.search(cohortQuery, request, response).get("results");
	//		Assert.assertEquals(2, results.size());
	//		Util.log("Found " + results.size() + " cohort(s)", results);
	//	}
	//	
	//	@Test
	//	public void voidCohort_shouldVoidACohort() throws Exception {
	//		Cohort cohort = service.getCohort(1);
	//		Assert.assertFalse(cohort.isVoided());
	//		controller.delete(cohortUuid, "unit test", request, response);
	//		cohort = service.getCohort(1);
	//		Assert.assertTrue(cohort.isVoided());
	//		Assert.assertEquals("unit test", cohort.getVoidReason());
	//	}
	//	
	//	@Test
	//	public void updateCohort_shouldChangeAPropertyOnACohort() throws Exception {
	//		String json = "{ \"name\":\"EXTRA COHORT\", \"description\":\"THIS IS NEW COHORT\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object editedCohort = controller.update(cohortUuid, post, request, response);
	//		Util.log("Edited cohort", editedCohort);
	//		Assert.assertEquals("EXTRA COHORT", service.getCohortByUuid(cohortUuid).getName());
	//	}
	//	
	//	@Test(expected = ConversionException.class)
	//	public void updateCohort_shouldFailToOverwriteMemberIdsOnACohort() throws Exception {
	//		Assert.assertEquals(3, service.getCohortByUuid(cohortUuid).getMemberIds().size());
	//		
	//		String json = "{ \"memberIds\": [ 2, 6 ] }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		controller.update(cohortUuid, post, request, response);
	//	}
	//	
	//	@Test
	//	public void purgeCohort_shouldPurgeCohort() throws Exception {
	//		int before = service.getAllCohorts().size();
	//		controller.purge(cohortUuid, request, response);
	//		Assert.assertEquals(before - 1, service.getAllCohorts().size());
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
