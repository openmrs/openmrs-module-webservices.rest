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

import org.openmrs.module.webservices.rest.web.v1_0.controller.ConceptController;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests functionality of {@link ConceptController}. This does not use @should annotations because
 * the controller inherits those methods from a subclass
 */
public class ConceptControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private ConceptService service;
	
	private ConceptController controller;
	
	private MockHttpServletRequest request;
	
	private HttpServletResponse response;
	
	@Before
	public void before() {
		this.service = Context.getConceptService();
		this.controller = new ConceptController();
		this.request = new MockHttpServletRequest();
		this.response = new MockHttpServletResponse();
	}
	
	@Test
	public void shouldGetAConceptByUuid() throws Exception {
		Object result = controller.retrieve("15f83cd6-64e9-4e06-a5f9-364d3b14a43d", request);
		Assert.assertNotNull(result);
		Assert.assertEquals("15f83cd6-64e9-4e06-a5f9-364d3b14a43d", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals("ASPIRIN", PropertyUtils.getProperty(PropertyUtils.getProperty(result, "name"), "name"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfYouTryToSpecifyDefaultRepOnGetConceptByUuid() throws Exception {
		request.setParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_DEFAULT);
		controller.retrieve("15f83cd6-64e9-4e06-a5f9-364d3b14a43d", request);
	}
	
	@Test
	public void shouldGetAConceptByName() throws Exception {
		Object result = controller.retrieve("TREATMENT STATUS", request);
		Assert.assertNotNull(result);
		Assert.assertEquals("511e03ab-7cbb-4b9f-abe3-d9256d67f27e", PropertyUtils.getProperty(result, "uuid"));
		Assert
		        .assertEquals("TREATMENT STATUS", PropertyUtils.getProperty(PropertyUtils.getProperty(result, "name"),
		            "name"));
	}
	
	@Test
	public void shouldListAllUnRetiredConcepts() throws Exception {
		int totalCount = service.getAllConcepts(null, true, true).size();
		
		SimpleObject result = controller.getAll(request, response);
		Assert.assertNotNull(result);
		Assert.assertTrue(totalCount > result.size());
		Assert.assertEquals(24, ((List<Object>) PropertyUtils.getProperty(result, "results")).size()); // there are 25 concepts and one is retired, so should only get 24 here
	}
	
	@Test
	public void shouldGetRefRepresentationForGetAllByDefault() throws Exception {
		SimpleObject result = controller.getAll(request, response);
		Object aResult = result.get(0);
		Assert.assertNull(PropertyUtils.getProperty(aResult, "datatype"));
	}
	
	@Test
	public void shouldGetSpecifiedRepresentationForGetAll() throws Exception {
		request.setParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_DEFAULT);
		SimpleObject result = controller.getAll(request, response);
		Object aResult = result.get(0);
		Assert.assertNotNull(PropertyUtils.getProperty(aResult, "datatype"));
	}
	
	@Test
	@Ignore("RESTWS-229: Define creatable/updatable properties on Concept, ConceptName, and ConceptDescription resources")
	public void shouldCreateAConcept() throws Exception {
		int originalCount = service.getAllConcepts().size();
		String json = "{ \"name\":\"test concept\", \"datatype\":\"8d4a4c94-c2cc-11de-8d13-0010c6dffd0f\", \"conceptClass\":\"Diagnosis\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object newConcept = controller.create(post, request, response);
		Assert.assertNotNull(PropertyUtils.getProperty(newConcept, "uuid"));
		Assert.assertEquals(originalCount + 1, service.getAllConcepts().size());
	}
	
	@Test
	@Ignore("RESTWS-229: Define creatable/updatable properties on Concept, ConceptName, and ConceptDescription resources")
	public void shouldEditFullySpecifiedNameOfAConcept() throws Exception {
		final String changedName = "TESTING NAME";
		String json = "{ \"name\":\"" + changedName + "\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.update("f923524a-b90c-4870-a948-4125638606fd", post, request, response);
		Concept updated = service.getConceptByUuid("f923524a-b90c-4870-a948-4125638606fd");
		Assert.assertNotNull(updated);
		Assert.assertEquals(changedName, updated.getFullySpecifiedName(Context.getLocale()).getName());
	}
	
	@Test
	@Ignore("RESTWS-229: Define creatable/updatable properties on Concept, ConceptName, and ConceptDescription resources")
	public void shouldEditAConcept() throws Exception {
		final String changedVersion = "1.2.3";
		String json = "{ \"version\":\"" + changedVersion + "\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.update("f923524a-b90c-4870-a948-4125638606fd", post, request, response);
		Concept updated = service.getConceptByUuid("f923524a-b90c-4870-a948-4125638606fd");
		Assert.assertNotNull(updated);
		Assert.assertEquals(changedVersion, updated.getVersion());
	}
	
	@Test
	public void shouldRetireAConcept() throws Exception {
		String uuid = "0a9afe04-088b-44ca-9291-0a8c3b5c96fa";
		Concept concept = service.getConceptByUuid(uuid);
		Assert.assertFalse(concept.isRetired());
		controller.delete(uuid, "really ridiculous random reason", request, response);
		concept = service.getConceptByUuid(uuid);
		Assert.assertTrue(concept.isRetired());
		Assert.assertEquals("really ridiculous random reason", concept.getRetireReason());
	}
	
	@Test
	public void shouldPurgeAConcept() throws Exception {
		int originalCount = service.getAllConcepts().size();
		String uuid = "11716f9c-1434-4f8d-b9fc-9aa14c4d6129";
		controller.purge(uuid, request, response);
		Assert.assertNull(service.getConceptByUuid(uuid));
		Assert.assertEquals(originalCount - 1, service.getAllConcepts().size());
	}
	
	@Test
	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
		MockHttpServletRequest httpReq = new MockHttpServletRequest();
		httpReq.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		Object result = controller.retrieve("0dde1358-7fcf-4341-a330-f119241a46e8", httpReq);
		Assert.assertNotNull(result);
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	@Test
	@Ignore
	//Ignored because H2 cannot execute the generated SQL because it requires all fetched columns to be included in the group by clause
	// see TRUNK-1956
	public void shouldSearchAndReturnAListOfConceptsMatchingTheQueryString() throws Exception {
		List<Object> hits = (List<Object>) controller.search("food", request, response).get("results");
		Assert.assertEquals(2, hits.size());
		Assert.assertEquals("0dde1358-7fcf-4341-a330-f119241a46e8", PropertyUtils.getProperty(hits.get(0), "uuid"));
		Assert.assertEquals("0f97e14e-cdc2-49ac-9255-b5126f8a5147", PropertyUtils.getProperty(hits.get(1), "uuid"));
	}
	
	/**
	 * {@link ConceptResource#getByUniqueId(String)}
	 * 
	 * @throws Exception
	 */
	@Test(expected = APIException.class)
	public void shouldFailToFetchAConceptByNameIfTheNameIsNeitherPreferredNorFullySpecified() throws Exception {
		///sanity test to ensure that actually a none retired concept exists with this name
		ConceptName name = Context.getConceptService().getConceptNameByUuid("8230adbf-30a9-4e18-b6d7-fc57e0c23cab");
		Assert.assertNotNull(name);
		Concept concept = Context.getConceptService().getConceptByName(name.getName());
		Assert.assertFalse(concept.isRetired());
		
		controller.retrieve(name.getName(), request);
	}
}
