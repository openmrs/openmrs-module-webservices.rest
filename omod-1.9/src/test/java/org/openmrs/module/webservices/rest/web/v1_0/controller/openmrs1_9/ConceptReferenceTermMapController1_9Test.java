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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptReferenceTermMap;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Rest1_9TestConstants;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest;

/**
 * Tests CRUD operations for {@link ConceptReferenceTerm}s via web service calls
 */
public class ConceptReferenceTermMapController1_9Test extends BaseCrudControllerTest {
	
	private RestHelperService service;
	
	@Override
	public String getURI() {
		return "conceptreferencetermmap";
	}
	
	@Override
	public String getUuid() {
		return Rest1_9TestConstants.CONCEPT_REFERENCE_TERM_MAP_UUID;
	}
	
	@Override
	public long getAllCount() {
		return 0; //not supported
	}
	
	@Before
	public void before() {
		service = Context.getService(RestHelperService.class);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#shouldGetAll()
	 */
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
	
	@Test
	public void shouldCreateConceptReferenceTermMap() throws Exception {
		String termA = Rest1_9TestConstants.CONCEPT_REFERENCE_TERM_UUID;
		String termB = Rest1_9TestConstants.CONCEPT_REFERENCE_TERM2_UUID;
		String json = "{\"termA\": \"" + termA + "\", " + "\"termB\": \"" + termB + "\", " + "\"conceptMapType\": \""
		        + Rest1_9TestConstants.CONCEPT_MAP_TYPE_UUID + "\"}";
		
		SimpleObject result = deserialize(handle(newPostRequest(getURI(), json)));
		
		String uuid = (String) result.get("uuid");
		ConceptReferenceTermMap termMap = service.getObjectByUuid(ConceptReferenceTermMap.class, uuid);
		assertThat(termMap.getTermA().getUuid(), is(termA));
		assertThat(termMap.getTermB().getUuid(), is(termB));
		assertThat(termMap.getConceptMapType().getUuid(), is(Rest1_9TestConstants.CONCEPT_MAP_TYPE_UUID));
	}
	
	@Test
	public void shouldEditingConceptReferenceTerm() throws Exception {
		String json = "{\"termB\": \"" + Rest1_9TestConstants.CONCEPT_REFERENCE_TERM2_UUID + "\"}";
		
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		ConceptReferenceTermMap termMap = service.getObjectByUuid(ConceptReferenceTermMap.class, getUuid());
		assertThat(termMap.getTermB().getUuid(), is(Rest1_9TestConstants.CONCEPT_REFERENCE_TERM2_UUID));
	}
	
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldNotDeleteConceptReferenceTermMap() throws Exception {
		handle(newDeleteRequest(getURI() + "/" + getUuid()));
	}
	
	@Test
	public void shouldPurgeConceptReferenceTerm() throws Exception {
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("purge", "")));
		assertNull(service.getObjectByUuid(ConceptReferenceTermMap.class, getUuid()));
	}
	
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldNotSearch() throws Exception {
		handle(newGetRequest(getURI(), new Parameter("q", "search query")));
	}
}
