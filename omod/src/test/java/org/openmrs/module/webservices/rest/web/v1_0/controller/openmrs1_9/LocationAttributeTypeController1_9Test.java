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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public class LocationAttributeTypeController1_9Test extends MainResourceControllerTest {
	
	/**
	 * @see MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "locationattributetype";
	}
	
	/**
	 * @see MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_9.LOCATION_ATTRIBUTE_TYPE_UUID;
	}
	
	/**
	 * @see MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return Context.getLocationService().getAllLocationAttributeTypes().size();
	}
	
	@BeforeEach
	public void before() throws Exception {
		executeDataSet(RestTestConstants1_9.TEST_DATASET);
	}
	
	/**
	 * @see LocationAttributeTypeController#createLocationAttributeType(SimpleObject,WebRequest)
	 * @verifies create a new LocationAttributeType
	 */
	@Test
	public void createLocationAttributeType_shouldCreateANewLocationAttributeType() throws Exception {
		int before = Context.getLocationService().getAllLocationAttributeTypes().size();
		String json = "{ \"name\":\"Some attributeType\",\"description\":\"Attribute Type for location\",\"datatypeClassname\":\"org.openmrs.customdatatype.datatype.FreeTextDatatype\"}";
		
		handle(newPostRequest(getURI(), json));
		
		Assertions.assertEquals(before + 1, Context.getLocationService().getAllLocationAttributeTypes().size());
	}
	
	/**
	 * @see LocationAttributeTypeController#updateLocationAttributeType(LocationAttributeType,SimpleObject,WebRequest)
	 * @verifies change a property on a location
	 */
	@Test
	public void updateLocationAttributeType_shouldChangeAPropertyOnALocationAttributeType() throws Exception {
		String json = "{\"description\":\"Updated description\"}";
		
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		
		Assertions.assertEquals("Updated description", Context.getLocationService().getLocationAttributeType(1).getDescription());
	}
	
	/**
	 * @see LocationAttributeTypeController#retireLocationAttributeType(LocationAttributeType,String,WebRequest)
	 * @verifies void a location attribute type
	 */
	@Test
	public void retireLocationAttributeType_shouldRetireALocationAttributeType() throws Exception {
		LocationAttributeType locationAttributeType = Context.getLocationService().getLocationAttributeType(1);
		Assertions.assertFalse(locationAttributeType.isRetired());
		
		MockHttpServletRequest request = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		request.addParameter("reason", "test");
		handle(request);
		
		locationAttributeType = Context.getLocationService().getLocationAttributeType(1);
		Assertions.assertTrue(locationAttributeType.isRetired());
		Assertions.assertEquals("test", locationAttributeType.getRetireReason());
	}
	
	/**
	 * @see LocationAttributeTypeController#findLocationAttributeTypes(String,WebRequest,HttpServletResponse)
	 * @verifies return no results if there are no matching location(s)
	 */
	@Test
	public void findLocationAttributeTypes_shouldReturnNoResultsIfThereAreNoMatchingLocations() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("q", "zzzznotype"))));
		
		Assertions.assertEquals(0, Util.getResultsSize(result));
	}
	
	/**
	 * @see LocationAttributeTypeController#findLocationAttributeTypes(String,WebRequest,HttpServletResponse)
	 * @verifies find matching location attribute types
	 */
	@Test
	public void findLocationAttributeTypes_shouldFindMatchingLocationAttributeTypes() throws Exception {
		SimpleObject response = deserialize(handle(newGetRequest(getURI(), new Parameter("q", "Audit"))));
		
		List<Object> results = Util.getResultsList(response);
		
		Assertions.assertEquals(1, results.size());
		Util.log("Found " + results.size() + " LocationAttributeType(s)", results);
		Object result = results.get(0);
		Assertions.assertEquals(RestTestConstants1_9.LOCATION_ATTRIBUTE_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
		Assertions.assertNotNull(PropertyUtils.getProperty(result, "links"));
		Assertions.assertNotNull(PropertyUtils.getProperty(result, "display"));
	}
	
}
