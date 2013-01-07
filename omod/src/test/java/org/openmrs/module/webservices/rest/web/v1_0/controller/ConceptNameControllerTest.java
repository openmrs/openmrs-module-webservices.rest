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

public class ConceptNameControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	String conceptUuid = "0cbe2ed3-cd5f-4f46-9459-26127c9265ab";
	//	
	//	String nameUuid = "b8159118-c97b-4d5a-a63e-d4aa4be0c4d3";
	//	
	//	String conceptUuid2 = "a09ab2c5-878e-4905-b25d-5784167d0216";
	//	
	//	private ConceptService service;
	//	
	//	private ConceptNameController controller;
	//	
	//	private MockHttpServletRequest request;
	//	
	//	private HttpServletResponse response;
	//	
	//	@Before
	//	public void before() throws Exception {
	//		this.service = Context.getConceptService();
	//		this.controller = new ConceptNameController();
	//		this.request = new MockHttpServletRequest();
	//		this.response = new MockHttpServletResponse();
	//	}
	//	
	//	@Test
	//	public void shouldGetAConceptName() throws Exception {
	//		Object result = controller.retrieve(conceptUuid, nameUuid, request);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals("COUGH SYRUP", PropertyUtils.getProperty(result, "name"));
	//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "uuid"));
	//	}
	//	
	//	@Test
	//	public void shouldAddNameToConcept() throws Exception {
	//		int before = service.getConceptByUuid(conceptUuid).getNames().size();
	//		String json = "{ \"name\":\"COUGH SYRUP II\", \"locale\":\"en\"}";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		controller.create(conceptUuid, post, request, response);
	//		int after = service.getConceptByUuid(conceptUuid).getNames().size();
	//		Assert.assertEquals(before + 1, after);
	//	}
	//	
	//	@Test
	//	public void shouldListNamesForAConcept() throws Exception {
	//		SimpleObject results = controller.getAll(conceptUuid2, request, response);
	//		List<Object> resultsList = (List<Object>) PropertyUtils.getProperty(results, "results");
	//		Assert.assertNotNull(results);
	//		Assert.assertEquals(3, resultsList.size());
	//		List<Object> names = Arrays.asList(PropertyUtils.getProperty(resultsList.get(0), "name"), PropertyUtils.getProperty(
	//		    resultsList.get(1), "name"), PropertyUtils.getProperty(resultsList.get(2), "name"));
	//		
	//		Assert.assertTrue(names.contains("CD4 COUNT"));
	//		Assert.assertTrue(names.contains("CD4"));
	//		Assert.assertTrue(names.contains("CD3+CD4+ABS CNT"));
	//	}
	//	
	//	@Test
	//	public void shouldEditAConceptName() throws Exception {
	//		SimpleObject results = controller.getAll(conceptUuid, request, response);
	//		List<Object> resultsList = (List<Object>) PropertyUtils.getProperty(results, "results");
	//		Assert.assertEquals(1, resultsList.size());
	//		ConceptName conceptName = service.getConceptNameByUuid(nameUuid);
	//		Assert.assertNotNull(conceptName);
	//		Assert.assertEquals("COUGH SYRUP", conceptName.getName());
	//		
	//		String json = "{ \"name\":\"NEW TEST NAME\"}";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		controller.update(conceptUuid, nameUuid, post, request, response);
	//		
	//		ConceptName updateConceptName = service.getConceptNameByUuid(nameUuid);
	//		//should have voided the old edited name
	//		Assert.assertTrue(updateConceptName.isVoided());
	//		SimpleObject results2 = controller.getAll(conceptUuid, request, response);
	//		List<Object> results2List = (List<Object>) PropertyUtils.getProperty(results2, "results");
	//		Assert.assertEquals(1, results2List.size());
	//		//should have created a new one with the new name
	//		Assert.assertTrue(PropertyUtils.getProperty(results2List.get(0), "name").equals("NEW TEST NAME"));
	//	}
	//	
	//	@Test
	//	public void shouldDeleteAConceptName() throws Exception {
	//		int before = service.getConceptByUuid(conceptUuid2).getNames().size();
	//		controller.delete(conceptUuid2, "8230adbf-30a9-4e18-b6d7-fc57e0c23cab", "testing", request, response);
	//		int after = service.getConceptByUuid(conceptUuid2).getNames().size();
	//		Assert.assertEquals(before - 1, after);
	//	}
	//	
	//	@Test
	//	public void shouldPurgeAConceptName() throws Exception {
	//		String conceptId = "5497";
	//		//using sql to be able to include voided names too
	//		Long before = (Long) Context.getAdministrationService().executeSQL(
	//		    "select count(*) from concept_name where concept_id = " + conceptId, true).get(0).get(0);
	//		controller.purge(conceptUuid2, "8230adbf-30a9-4e18-b6d7-fc57e0c23cab", request, response);
	//		Long after = (Long) Context.getAdministrationService().executeSQL(
	//		    "select count(*) from concept_name where concept_id = " + conceptId, true).get(0).get(0);
	//		Assert.assertEquals(before.longValue() - 1, after.longValue());
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
