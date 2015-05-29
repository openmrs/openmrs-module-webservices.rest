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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests CRUD operations for {@link ConceptMapType}s via web service calls
 */
public class ConceptMapController1_9Test extends MainResourceControllerTest {
	
	private ConceptService service;
	
	private RestHelperService restHelperService;
	
	@Override
	public String getURI() {
		return "concept/" + RestTestConstants1_9.CONCEPT_UUID + "/mapping";
	}
	
	@Override
	public String getUuid() {
		return RestTestConstants1_9.CONCEPT_MAP_UUID;
	}
	
	@Override
	public long getAllCount() {
		return service.getConceptByUuid(RestTestConstants1_9.CONCEPT_UUID).getConceptMappings().size();
	}
	
	@Before
	public void before() {
		service = Context.getConceptService();
		restHelperService = Context.getService(RestHelperService.class);
	}
	
	@Test
	public void shouldCreateConceptMap() throws Exception {
		String json = "{\"conceptReferenceTerm\": \"" + RestTestConstants1_9.CONCEPT_REFERENCE_TERM_UUID
		        + "\", \"conceptMapType\": \"" + RestTestConstants1_9.CONCEPT_MAP_TYPE_UUID + "\"}";
		
		SimpleObject newConceptMap = deserialize(handle(newPostRequest(getURI(), json)));
		
		String uuid = (String) newConceptMap.get("uuid");
		
		ConceptMap conceptMap = restHelperService.getObjectByUuid(ConceptMap.class, uuid);
		assertThat(conceptMap.getConcept().getUuid(), is(RestTestConstants1_9.CONCEPT_UUID));
		assertThat(conceptMap.getConceptMapType().getUuid(), is(RestTestConstants1_9.CONCEPT_MAP_TYPE_UUID));
		assertThat(conceptMap.getConceptReferenceTerm().getUuid(), is(RestTestConstants1_9.CONCEPT_REFERENCE_TERM_UUID));
	}
	
	@Test
	public void shouldEditConceptMap() throws Exception {
		String json = "{\"conceptReferenceTerm\": \"" + RestTestConstants1_9.CONCEPT_REFERENCE_TERM_UUID + "\"}";
		
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		
		ConceptMap conceptMap = restHelperService.getObjectByUuid(ConceptMap.class, getUuid());
		assertThat(conceptMap.getConceptReferenceTerm().getUuid(), is(RestTestConstants1_9.CONCEPT_REFERENCE_TERM_UUID));
	}
	
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldNotDeleteConceptMap() throws Exception {
		handle(newDeleteRequest(getURI() + "/" + getUuid()));
	}
	
	@Test
	public void shouldPurgeConceptMap() throws Exception {
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("purge", "")));
		assertNull(restHelperService.getObjectByUuid(ConceptMap.class, getUuid()));
	}
	
	@Test
	public void shouldSearchAndReturnAListOfConceptMapTypesMatchingTheQueryString() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("q", "is");
		SimpleObject result = deserialize(handle(req));
		assertEquals(2, Util.getResultsSize(result));
	}
}
