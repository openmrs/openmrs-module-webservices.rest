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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptDescription;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ConceptDescriptionResource;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class ConceptDescriptionControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	String conceptUuid = "b055abd8-a420-4a11-8b98-02ee170a7b54";
	//	
	//	String descriptionUuid = "be3321b3-c1c7-4339-aaca-1b60db12e1df";
	//	
	//	private ConceptService service;
	//	
	//	private ConceptDescriptionController controller;
	//	
	//	private MockHttpServletRequest request;
	//	
	//	private HttpServletResponse response;
	//	
	//	@Before
	//	public void before() throws Exception {
	//		this.service = Context.getConceptService();
	//		this.controller = new ConceptDescriptionController();
	//		this.request = new MockHttpServletRequest();
	//		this.response = new MockHttpServletResponse();
	//	}
	//	
	//	/**
	//	 * @See {@link ConceptDescriptionResource#getByUniqueId(String)}
	//	 * @throws Exception
	//	 */
	//	@Test
	//	public void shouldGetAConceptDescriptionWhenRepresentationIsSetToRef() throws Exception {
	//		request.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_REF);
	//		Object result = controller.retrieve(conceptUuid, descriptionUuid, request);
	//		Assert.assertNotNull(result);
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertEquals("Affirmative", PropertyUtils.getProperty(result, "display"));
	//	}
	//	
	//	/**
	//	 * @See {@link ConceptDescriptionResource#getByUniqueId(String)}
	//	 * @throws Exception
	//	 */
	//	@Test
	//	public void shouldGetAConceptDescription() throws Exception {
	//		Object result = controller.retrieve(conceptUuid, descriptionUuid, request);
	//		Assert.assertNotNull(result);
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertEquals("Affirmative", PropertyUtils.getProperty(result, "description"));
	//		Assert.assertEquals("en", PropertyUtils.getProperty(result, "locale"));
	//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	/**
	//	 * @See {@link ConceptDescriptionResource#getByUniqueId(String)}
	//	 * @throws Exception
	//	 */
	//	@Test
	//	public void shouldGetAConceptDescriptionWhenRepresentationIsSetToFull() throws Exception {
	//		request.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//		Object result = controller.retrieve(conceptUuid, descriptionUuid, request);
	//		Assert.assertNotNull(result);
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertEquals("Affirmative", PropertyUtils.getProperty(result, "description"));
	//		Assert.assertEquals("en", PropertyUtils.getProperty(result, "locale"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	/**
	//	 * @See {@link ConceptDescriptionResource#create(String, SimpleObject, org.openmrs.module.webservices.rest.web.RequestContext)}
	//	 * @throws Exception
	//	 */
	//	@Test
	//	public void shouldAddADescriptionToConcept() throws Exception {
	//		int before = service.getConceptByUuid(conceptUuid).getDescriptions().size();
	//		String json = "{ \"description\":\"New Description\", \"locale\":\"fr\"}";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		controller.create(conceptUuid, post, request, response);
	//		int after = service.getConceptByUuid(conceptUuid).getDescriptions().size();
	//		Assert.assertEquals(before + 1, after);
	//	}
	//	
	//	/**
	//	 * @See {@link ConceptDescriptionResource#doGetAll(Concept, org.openmrs.module.webservices.rest.web.RequestContext)}
	//	 * @throws Exception
	//	 */
	//	@Test
	//	public void shouldListDescriptionsForAConcept() throws Exception {
	//		//Add one more description for testing purposes
	//		Concept testConcept = Context.getConceptService().getConceptByUuid(conceptUuid);
	//		ConceptDescription testDescription = new ConceptDescription("another description", new Locale("fr"));
	//		testConcept.addDescription(testDescription);
	//		Context.getConceptService().saveConcept(testConcept);
	//		Assert.assertNotNull(testDescription.getConceptDescriptionId());
	//		Assert.assertEquals(2, testConcept.getDescriptions().size());
	//		
	//		SimpleObject results = controller.getAll(conceptUuid, request, response);
	//		List<Object> resultsList = (List<Object>) PropertyUtils.getProperty(results, "results");
	//		Assert.assertNotNull(results);
	//		Assert.assertEquals(2, resultsList.size());
	//		List<Object> descriptions = Arrays.asList(PropertyUtils.getProperty(resultsList.get(0), "description"),
	//		    PropertyUtils.getProperty(resultsList.get(1), "description"));
	//		
	//		Assert.assertTrue(descriptions.contains("Affirmative"));
	//		Assert.assertTrue(descriptions.contains("another description"));
	//	}
	//	
	//	/**
	//	 * @See {@link ConceptDescriptionResource#update(String, String, SimpleObject, org.openmrs.module.webservices.rest.web.RequestContext)}
	//	 * @throws Exception
	//	 */
	//	@Test
	//	public void shouldEditAConceptDescription() throws Exception {
	//		SimpleObject results = controller.getAll(conceptUuid, request, response);
	//		List<Object> resultsList = (List<Object>) PropertyUtils.getProperty(results, "results");
	//		Assert.assertEquals(1, resultsList.size());
	//		ConceptDescription conceptDescription = service.getConceptDescriptionByUuid(descriptionUuid);
	//		Assert.assertEquals("Affirmative", conceptDescription.getDescription());
	//		
	//		String json = "{ \"description\":\"NEW TEST DESCRIPTION\"}";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		controller.update(conceptUuid, descriptionUuid, post, request, response);
	//		
	//		//should have created a new one with the new description
	//		Assert.assertTrue(PropertyUtils.getProperty(conceptDescription, "description").equals("NEW TEST DESCRIPTION"));
	//	}
	//	
	//	/**
	//	 * This tests that delete always delegates to
	//	 * {@link ConceptDescriptionResource#purge(ConceptDescription, org.openmrs.module.webservices.rest.web.RequestContext)}
	//	 * since descriptions are not retirable/voidable
	//	 * 
	//	 * @see {@link ConceptDescriptionResource#delete(ConceptDescription, String, org.openmrs.module.webservices.rest.web.RequestContext)}
	//	 * @throws Exception
	//	 */
	//	@Test
	//	public void shouldDeleteAConceptDescription() throws Exception {
	//		int before = service.getConceptByUuid(conceptUuid).getDescriptions().size();
	//		controller.delete(conceptUuid, descriptionUuid, "testing", request, response);
	//		int after = service.getConceptByUuid(conceptUuid).getDescriptions().size();
	//		Assert.assertEquals(before - 1, after);
	//	}
	//	
	//	/**
	//	 * @See {@link ConceptDescriptionResource#purge(ConceptDescription, org.openmrs.module.webservices.rest.web.RequestContext)}
	//	 * @throws Exception
	//	 */
	//	@Test
	//	public void shouldPurgeAConceptDescription() throws Exception {
	//		int before = service.getConceptByUuid(conceptUuid).getDescriptions().size();
	//		controller.delete(conceptUuid, descriptionUuid, "testing", request, response);
	//		int after = service.getConceptByUuid(conceptUuid).getDescriptions().size();
	//		Assert.assertEquals(before - 1, after);
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
