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

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.CohortService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Tests functionality of {@link CohortController}.
 */
public class CohortControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private static final String cohortUUID = "05e08b3b-5690-41e1-b651-5391fd946c1a";
	
	private static final String cohortName = "B13 deficit";
	
	private static final String datasetFilename = "customTestDataset.xml";
	
	private CohortService service;
	
	private CohortController controller;
	
	private WebRequest request;
	
	private HttpServletResponse response;
	
	@Before
	public void before() throws Exception {
		this.service = Context.getCohortService();
		this.controller = new CohortController();
		this.request = new ServletWebRequest(new MockHttpServletRequest());
		this.response = new MockHttpServletResponse();
		executeXmlDataSet(datasetFilename);
	}
	
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
	
	@Test
	public void createCohort_shouldCreateANewCohort() throws Exception {
		int before = service.getAllCohorts().size();
		String json = "{ \"name\":\"NEW COHORT\", \"description\":\"THIS IS NEW COHORT\", \"memberIds\": [ 2, 6 ]}";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object newCohort = controller.create(post, request, response);
		log("Created cohort", newCohort);
		Assert.assertEquals(before + 1, service.getAllCohorts().size());
	}
	
	@Test
	public void getCohort_shouldGetADefaultRepresentationOfACohort() throws Exception {
		Object result = controller.retrieve(cohortUUID, request);
		Assert.assertNotNull(result);
		log("Cohort fetched (default)", result);
		Assert.assertEquals(cohortUUID, PropertyUtils.getProperty(result, "uuid"));
	}
	
	@Test
	public void getCohortByFuzzyName_shouldGetADefaultRepresentationOfACohort() throws Exception {
		Object result = controller.retrieve(cohortName, request);
		Assert.assertNotNull(result);
		log("Cohort fetched (default)", result);
		Assert.assertEquals(cohortName, PropertyUtils.getProperty(result, "name"));
	}
	
	@Test
	public void voidCohort_shouldVoidACohort() throws Exception {
		Cohort cohort = service.getCohort(1);
		Assert.assertFalse(cohort.isVoided());
		controller.delete(cohortUUID, "unit test", request, response);
		cohort = service.getCohort(1);
		Assert.assertTrue(cohort.isVoided());
		Assert.assertEquals("unit test", cohort.getVoidReason());
	}
	
	@Test
	public void updateCohort_shouldChangeAPropertyOnACohort() throws Exception {
		
		String json = "{ \"name\":\"EXTRA COHORT\", \"description\":\"THIS IS NEW COHORT\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object editedCohort = controller.update(cohortUUID, post, request, response);
		log("Edited cohort", editedCohort);
		Assert.assertEquals("EXTRA COHORT", service.getCohortByUuid(cohortUUID).getName());
	}
	
	@Test()
	public void purgeCohort_shouldPurgeCohort() throws Exception {
		int before = service.getAllCohorts().size();
		controller.purge(cohortUUID, request, response);
		Assert.assertEquals(before - 1, service.getAllCohorts().size());
	}
}
