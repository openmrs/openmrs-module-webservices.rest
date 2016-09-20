/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.LocationAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class LocationAttributeTypeController1_9Test extends MainResourceControllerTest {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "locationattributetype";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_9.LOCATION_ATTRIBUTE_TYPE_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return Context.getLocationService().getAllLocationAttributeTypes().size();
	}
	
	@Before
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
		
		Assert.assertEquals(before + 1, Context.getLocationService().getAllLocationAttributeTypes().size());
	}
	
	/**
	 * @see LocationAttributeTypeController#updateLocationAttributeType(LocationAttributeType,SimpleObject,WebRequest)
	 * @verifies change a property on a location
	 */
	@Test
	public void updateLocationAttributeType_shouldChangeAPropertyOnALocationAttributeType() throws Exception {
		String json = "{\"description\":\"Updated description\"}";
		
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		
		Assert.assertEquals("Updated description", Context.getLocationService().getLocationAttributeType(1).getDescription());
	}
	
	/**
	 * @see LocationAttributeTypeController#retireLocationAttributeType(LocationAttributeType,String,WebRequest)
	 * @verifies void a location attribute type
	 */
	@Test
	public void retireLocationAttributeType_shouldRetireALocationAttributeType() throws Exception {
		LocationAttributeType locationAttributeType = Context.getLocationService().getLocationAttributeType(1);
		Assert.assertFalse(locationAttributeType.isRetired());
		
		MockHttpServletRequest request = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		request.addParameter("reason", "test");
		handle(request);
		
		locationAttributeType = Context.getLocationService().getLocationAttributeType(1);
		Assert.assertTrue(locationAttributeType.isRetired());
		Assert.assertEquals("test", locationAttributeType.getRetireReason());
	}
	
	/**
	 * @see LocationAttributeTypeController#findLocationAttributeTypes(String,WebRequest,HttpServletResponse)
	 * @verifies return no results if there are no matching location(s)
	 */
	@Test
	public void findLocationAttributeTypes_shouldReturnNoResultsIfThereAreNoMatchingLocations() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("q", "zzzznotype"))));
		
		Assert.assertEquals(0, Util.getResultsSize(result));
	}
	
	/**
	 * @see LocationAttributeTypeController#findLocationAttributeTypes(String,WebRequest,HttpServletResponse)
	 * @verifies find matching location attribute types
	 */
	@Test
	public void findLocationAttributeTypes_shouldFindMatchingLocationAttributeTypes() throws Exception {
		SimpleObject response = deserialize(handle(newGetRequest(getURI(), new Parameter("q", "Audit"))));
		
		List<Object> results = Util.getResultsList(response);
		
		Assert.assertEquals(1, results.size());
		Util.log("Found " + results.size() + " LocationAttributeType(s)", results);
		Object result = results.get(0);
		Assert.assertEquals(RestTestConstants1_9.LOCATION_ATTRIBUTE_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
	}
	
}
