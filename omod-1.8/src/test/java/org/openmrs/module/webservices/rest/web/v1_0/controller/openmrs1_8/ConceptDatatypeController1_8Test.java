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
import org.openmrs.ConceptDatatype;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests functionality of {@link ConceptDatatypeController}.
 */
public class ConceptDatatypeController1_8Test extends MainResourceControllerTest {
	
	private ConceptService service;
	
	@Before
	public void before() {
		this.service = Context.getConceptService();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "conceptdatatype";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return service.getAllConceptDatatypes().size();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_8.CONCEPT_DATATYPE_UUID;
	}
	
	@Test
	public void shouldGetAConceptDatatypeByUuid() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		
		ConceptDatatype conceptDataType = service.getConceptDatatypeByUuid(getUuid());
		Assert.assertEquals(conceptDataType.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals(conceptDataType.getName(), PropertyUtils.getProperty(result, "name"));
	}
	
	@Test
	public void shouldGetAConceptDatatypeByName() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/Coded");
		SimpleObject result = deserialize(handle(req));
		
		ConceptDatatype conceptDataType = service.getConceptDatatypeByName("Coded");
		Assert.assertEquals(conceptDataType.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals(conceptDataType.getName(), PropertyUtils.getProperty(result, "name"));
	}
	
	@Test
	public void shouldListAllConceptDatatypes() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Assert.assertEquals(getAllCount(), Util.getResultsSize(result));
	}
	
	@Test
	public void shouldCreateAConceptDatatype() throws Exception {
		long originalCount = getAllCount();
		
		SimpleObject conceptDataType = new SimpleObject();
		conceptDataType.add("name", "test name");
		conceptDataType.add("description", "test description");
		
		String json = new ObjectMapper().writeValueAsString(conceptDataType);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		SimpleObject newConceptDatatype = deserialize(handle(req));
		
		Assert.assertNotNull(PropertyUtils.getProperty(newConceptDatatype, "uuid"));
		Assert.assertEquals(originalCount + 1, getAllCount());
	}
	
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldNotSupportEditingAConceptDatatype() throws Exception {
		
		SimpleObject conceptDataType = new SimpleObject();
		conceptDataType.add("name", "updated name");
		
		String json = new ObjectMapper().writeValueAsString(conceptDataType);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/" + getUuid());
		req.setContent(json.getBytes());
		handle(req);
		
	}
	
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldNotSupportRetiringAConceptDatatype() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		req.addParameter("!purge", "");
		handle(req);
	}
	
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldNotSupportPurgingAConceptDatatype() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		req.addParameter("purge", "");
		handle(req);
	}
	
	@Test
	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
}
