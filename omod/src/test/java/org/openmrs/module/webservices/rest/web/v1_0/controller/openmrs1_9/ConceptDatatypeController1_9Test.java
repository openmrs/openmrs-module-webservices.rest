/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import org.apache.commons.beanutils.PropertyUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class ConceptDatatypeController1_9Test extends MainResourceControllerTest {
	
	private ConceptService service;
	
	@BeforeEach
	public void before() {
		this.service = Context.getConceptService();
	}
	
	/**
	 * @see MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "conceptdatatype";
	}
	
	/**
	 * @see MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return service.getAllConceptDatatypes().size();
	}
	
	/**
	 * @see MainResourceControllerTest#getUuid()
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
		Assertions.assertEquals(conceptDataType.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assertions.assertEquals(conceptDataType.getName(), PropertyUtils.getProperty(result, "name"));
	}
	
	@Test
	public void shouldGetAConceptDatatypeByName() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/Coded");
		SimpleObject result = deserialize(handle(req));
		
		ConceptDatatype conceptDataType = service.getConceptDatatypeByName("Coded");
		Assertions.assertEquals(conceptDataType.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assertions.assertEquals(conceptDataType.getName(), PropertyUtils.getProperty(result, "name"));
	}
	
	@Test
	public void shouldListAllConceptDatatypes() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(getAllCount(), Util.getResultsSize(result));
	}
	
	@Test
	public void shouldCreateAConceptDatatype() throws Exception {
		assertThrows(ResourceDoesNotSupportOperationException.class, () -> {
			SimpleObject conceptDataType = new SimpleObject();
			conceptDataType.add("name", "test name");
			conceptDataType.add("description", "test description");
		
			String json = new ObjectMapper().writeValueAsString(conceptDataType);
		
			MockHttpServletRequest req = request(RequestMethod.POST, getURI());
			req.setContent(json.getBytes());
		
			handle(req);
		});
	}
	
	@Test
	public void shouldNotSupportEditingAConceptDatatype() throws Exception {
		assertThrows(ResourceDoesNotSupportOperationException.class, () -> {
		
			SimpleObject conceptDataType = new SimpleObject();
			conceptDataType.add("name", "updated name");
		
			String json = new ObjectMapper().writeValueAsString(conceptDataType);
		
			MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/" + getUuid());
			req.setContent(json.getBytes());
			handle(req);
		
		});
	}
	
	@Test
	public void shouldNotSupportRetiringAConceptDatatype() throws Exception {
		assertThrows(ResourceDoesNotSupportOperationException.class, () -> {
			MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
			req.addParameter("!purge", "");
			handle(req);
		});
	}
	
	@Test
	public void shouldNotSupportPurgingAConceptDatatype() throws Exception {
		assertThrows(ResourceDoesNotSupportOperationException.class, () -> {
			MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
			req.addParameter("purge", "true");
			handle(req);
		});
	}
	
	@Test
	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(req));
		
		Assertions.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
}
