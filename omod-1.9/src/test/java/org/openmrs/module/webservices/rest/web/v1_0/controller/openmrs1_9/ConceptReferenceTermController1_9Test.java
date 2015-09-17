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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests CRUD operations for {@link ConceptReferenceTerm}s via web service calls
 */
public class ConceptReferenceTermController1_9Test extends MainResourceControllerTest {
	
	private ConceptService service;
	
	@Override
	public String getURI() {
		return "conceptreferenceterm";
	}
	
	@Override
	public String getUuid() {
		return RestTestConstants1_9.CONCEPT_REFERENCE_TERM_UUID;
	}
	
	@Override
	public long getAllCount() {
		return service.getConceptReferenceTerms(false).size();
	}
	
	@Before
	public void before() {
		this.service = Context.getConceptService();
	}
	
	@Test
	public void shouldGetAnConceptReferenceTermByUuid() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		
		ConceptReferenceTerm conceptReferenceTermType = service.getConceptReferenceTermByUuid(getUuid());
		assertEquals(conceptReferenceTermType.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		assertEquals(conceptReferenceTermType.getCode(), PropertyUtils.getProperty(result, "code"));
	}
	
	@Test
	public void shouldListAllConceptReferenceTerms() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		
		assertNotNull(result);
		assertEquals(getAllCount(), Util.getResultsSize(result));
	}
	
	@Test
	public void shouldCreateAConceptReferenceTerm() throws Exception {
		long originalCount = getAllCount();
		
		SimpleObject conceptReferenceTermType = new SimpleObject();
		conceptReferenceTermType.add("code", "test code");
		conceptReferenceTermType.add("conceptSource", "00001827-639f-4cb4-961f-1e025bf80000");
		
		String json = new ObjectMapper().writeValueAsString(conceptReferenceTermType);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		SimpleObject newConceptReferenceTerm = deserialize(handle(req));
		
		assertNotNull(PropertyUtils.getProperty(newConceptReferenceTerm, "uuid"));
		assertEquals(originalCount + 1, getAllCount());
	}
	
	@Test
	public void shouldEditingAConceptReferenceTerm() throws Exception {
		final String newCode = "updated code";
		SimpleObject conceptReferenceTermType = new SimpleObject();
		conceptReferenceTermType.add("code", newCode);
		
		String json = new ObjectMapper().writeValueAsString(conceptReferenceTermType);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/" + getUuid());
		req.setContent(json.getBytes());
		handle(req);
		assertEquals(newCode, service.getConceptReferenceTermByUuid(getUuid()).getCode());
	}
	
	@Test
	public void shouldRetireAConceptReferenceTerm() throws Exception {
		assertEquals(false, service.getConceptReferenceTermByUuid(getUuid()).isRetired());
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		req.addParameter("!purge", "");
		final String reason = "none";
		req.addParameter("reason", reason);
		handle(req);
		assertEquals(true, service.getConceptReferenceTermByUuid(getUuid()).isRetired());
		assertEquals(reason, service.getConceptReferenceTermByUuid(getUuid()).getRetireReason());
	}
	
	@Test
	public void shouldPurgeAConceptReferenceTerm() throws Exception {
		final String uuid = "SSTRM-retired code";
		assertNotNull(service.getConceptReferenceTermByUuid(uuid));
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + uuid);
		req.addParameter("purge", "");
		handle(req);
		assertNull(service.getConceptReferenceTermByUuid(uuid));
	}
	
	@Test
	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));
		
		assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	@Test
	public void shouldSearchAndReturnAListOfConceptReferenceTermsMatchingTheQueryString() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("q", "cd4");
		SimpleObject result = deserialize(handle(req));
		assertEquals(3, Util.getResultsSize(result));
	}
	
	@Test
	public void shouldFindBySourceName() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("source",
		        "Some Standardized Terminology"))));
		Integer resultsSize = Util.getResultsSize(result);
		assertThat(resultsSize, is(8));
	}
	
	@Test
	public void shouldFindBySourceUuid() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("source",
		        "00001827-639f-4cb4-961f-1e025bf80000"))));
		Integer resultsSize = Util.getResultsSize(result);
		assertThat(resultsSize, is(8));
	}
	
	@Test
	public void shouldFindBySourceAndCode() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("source",
		        "Some Standardized Terminology"), new Parameter("code", "WGT234"))));
		assertThat(Util.getResultsSize(result), is(1));
		List<Object> results = Util.getResultsList(result);
		assertThat(BeanUtils.getProperty(results.get(0), "uuid"), is("SSTRM-WGT234"));
	}
	
	@Test
	public void shouldFindBySourceAndName() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("source",
		        "Some Standardized Terminology"), new Parameter("name", "weight term"))));
		assertThat(Util.getResultsSize(result), is(1));
		List<Object> results = Util.getResultsList(result);
		assertThat(BeanUtils.getProperty(results.get(0), "uuid"), is("SSTRM-WGT234"));
	}
}
