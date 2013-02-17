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

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ResourceTestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests functionality of {@link DrugController}. This does not use @should annotations because
 * the controller inherits those methods from a subclass
 */
public class DrugControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	private ConceptService service;
	//	
	//	private DrugController controller;
	//	
	//	private MockHttpServletRequest request;
	//	
	//	private HttpServletResponse response;
	//	
	//	@Before
	//	public void before() {
	//		this.service = Context.getConceptService();
	//		this.controller = new DrugController();
	//		this.request = new MockHttpServletRequest();
	//		this.response = new MockHttpServletResponse();
	//	}
	//	
	//	@Test
	//	public void shouldGetADrugByUuid() throws Exception {
	//		Object result = controller.retrieve(ResourceTestConstants.DRUG_UUID, request);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals(ResourceTestConstants.DRUG_UUID, PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertEquals("Aspirin", PropertyUtils.getProperty(result, "name"));
	//	}
	//	
	//	@Test
	//	public void shouldListAllUnRetiredDrugs() throws Exception {
	//		int totalCount = service.getAllDrugs(true).size();
	//		
	//		SimpleObject result = controller.getAll(request, response);
	//		Assert.assertNotNull(result);
	//		Assert.assertTrue(totalCount > result.size());
	//		Assert.assertEquals(2, Util.getResultsSize(result));
	//	}
	//	
	//	@Test
	//	@Ignore("RESTWS-230: Define creatable/updatable properties on Drug resource")
	//	public void shouldCreateADrug() throws Exception {
	//		int originalCount = service.getAllDrugs().size();
	//		String json = "{ \"name\":\"test drug\", \"concept\":\"15f83cd6-64e9-4e06-a5f9-364d3b14a43d\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object newConcept = controller.create(post, request, response);
	//		Assert.assertNotNull(PropertyUtils.getProperty(newConcept, "uuid"));
	//		Assert.assertEquals(originalCount + 1, service.getAllDrugs().size());
	//	}
	//	
	//	@Test
	//	@Ignore("RESTWS-230: Define creatable/updatable properties on Drug resource")
	//	public void shouldEditADrug() throws Exception {
	//		final String changedDescription = "some description";
	//		String json = "{ \"description\":\"" + changedDescription + "\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		controller.update(ResourceTestConstants.DRUG_UUID, post, request, response);
	//		Drug updated = service.getDrugByUuid(ResourceTestConstants.DRUG_UUID);
	//		Assert.assertNotNull(updated);
	//		Assert.assertEquals(changedDescription, updated.getDescription());
	//	}
	//	
	//	@Test
	//	public void shouldRetireADrug() throws Exception {
	//		Drug drug = service.getDrugByUuid(ResourceTestConstants.DRUG_UUID);
	//		Assert.assertFalse(drug.isRetired());
	//		controller.delete(ResourceTestConstants.DRUG_UUID, "really ridiculous random reason", request, response);
	//		drug = service.getDrugByUuid(ResourceTestConstants.DRUG_UUID);
	//		Assert.assertTrue(drug.isRetired());
	//		Assert.assertEquals("really ridiculous random reason", drug.getRetireReason());
	//	}
	//	
	//	@Test
	//	@Ignore("RESTWS-230: Define creatable/updatable properties on Drug resource")
	//	public void shouldPurgeADrug() throws Exception {
	//		String json = "{ \"name\":\"test drug\", \"concept\":\"15f83cd6-64e9-4e06-a5f9-364d3b14a43d\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object newDrug = controller.create(post, request, response);
	//		String drugUuid = (String) PropertyUtils.getProperty(newDrug, "uuid");
	//		
	//		int originalCount = service.getAllDrugs().size();
	//		controller.purge(drugUuid, request, response);
	//		Assert.assertNull(service.getDrugByUuid(drugUuid));
	//		Assert.assertEquals(originalCount - 1, service.getAllDrugs().size());
	//	}
	//	
	//	@Test
	//	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
	//		MockHttpServletRequest httpReq = new MockHttpServletRequest();
	//		httpReq.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//		Object result = controller.retrieve(ResourceTestConstants.DRUG_UUID, httpReq);
	//		Assert.assertNotNull(result);
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
