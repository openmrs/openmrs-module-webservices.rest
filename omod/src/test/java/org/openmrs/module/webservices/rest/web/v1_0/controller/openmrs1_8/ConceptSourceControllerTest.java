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

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ResourceTestConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests functionality of {@link ConceptSourceController}.
 */
public class ConceptSourceControllerTest extends BaseCrudControllerTest {
	
	private ConceptService service;
	
	@Before
	public void before() {
		this.service = Context.getConceptService();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "conceptsource";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return service.getAllConceptSources().size();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return ResourceTestConstants.CONCEPT_SOURCE_UUID;
	}
	
	@Test
	public void shouldGetAConceptSourceByUuid() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		
		ConceptSource conceptSource = service.getConceptSourceByUuid(getUuid());
		Assert.assertEquals(conceptSource.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals(conceptSource.getName(), PropertyUtils.getProperty(result, "name"));
		Assert.assertEquals(conceptSource.getHl7Code(), PropertyUtils.getProperty(result, "hl7Code"));
		Assert.assertEquals(conceptSource.getDescription(), PropertyUtils.getProperty(result, "description"));
	}
	
	@Test
	public void shouldGetAConceptSourceByName() throws Exception {
		final String name = "ICD-10";
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + name);
		SimpleObject result = deserialize(handle(req));
		
		ConceptSource conceptSource = service.getConceptSourceByName(name);
		Assert.assertEquals(conceptSource.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals(conceptSource.getName(), PropertyUtils.getProperty(result, "name"));
	}
	
	@Test
	public void shouldListAllConceptSources() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Assert.assertEquals(getAllCount(), Util.getResultsSize(result));
	}
	
	@Test
	public void shouldCreateAConceptSource() throws Exception {
		long originalCount = getAllCount();
		
		SimpleObject conceptSource = new SimpleObject();
		conceptSource.add("name", "test name");
		conceptSource.add("description", "test description");
		
		String json = new ObjectMapper().writeValueAsString(conceptSource);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		SimpleObject newConceptSource = deserialize(handle(req));
		
		Assert.assertNotNull(PropertyUtils.getProperty(newConceptSource, "uuid"));
		Assert.assertEquals(originalCount + 1, getAllCount());
	}
	
	@Test
	public void shouldEditAConceptSource() throws Exception {
		final String newName = "updated name";
		SimpleObject conceptSource = new SimpleObject();
		conceptSource.add("name", newName);
		
		String json = new ObjectMapper().writeValueAsString(conceptSource);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/" + getUuid());
		req.setContent(json.getBytes());
		handle(req);
		Assert.assertEquals(newName, service.getConceptSourceByUuid(getUuid()).getName());
	}
	
	@Test
	public void shouldRetireAConceptSource() throws Exception {
		Assert.assertEquals(false, service.getConceptSourceByUuid(getUuid()).isRetired());
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		req.addParameter("!purge", "");
		final String reason = "none";
		req.addParameter("reason", reason);
		handle(req);
		Assert.assertEquals(true, service.getConceptSourceByUuid(getUuid()).isRetired());
		Assert.assertEquals(reason, service.getConceptSourceByUuid(getUuid()).getRetireReason());
	}
	
	@Test
	public void shouldPurgeAConceptSource() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		req.addParameter("purge", "");
		handle(req);
		Assert.assertNull(service.getConceptSourceByUuid(getUuid()));
	}
	
	@Test
	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	@Test
	public void shouldSearchAConceptSourceIfItMatchesTheQuery() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("q", "ICD-10");
		SimpleObject result = deserialize(handle(req));
		Assert.assertEquals(1, Util.getResultsSize(result));
		Assert.assertEquals(getUuid(), PropertyUtils.getProperty(Util.getResultsList(result).get(0), "uuid"));
	}
	
}
