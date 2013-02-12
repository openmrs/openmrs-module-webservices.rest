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

import org.junit.Test;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

public class LocationAttributeTypeControllerTest extends BaseModuleWebContextSensitiveTest {
	
	@Test
	public void fakeTest() {
		
	}
	
//	@Before
//	public void before() throws Exception {
//		executeDataSet(Rest19ExtTestConstants.TEST_DATASET);
//	}
//	
//	/**
//	 * @see
//	 * LocationAttributeTypeController#createLocationAttributeType(SimpleObject,WebRequest)
//	 * @verifies create a new LocationAttributeType
//	 */
//	@Test
//	public void createLocationAttributeType_shouldCreateANewLocationAttributeType() throws Exception {
//		int before = Context.getLocationService().getAllLocationAttributeTypes().size();
//		String json = "{ \"name\":\"Some attributeType\",\"description\":\"Attribute Type for location\",\"datatypeClassname\":\"org.openmrs.customdatatype.datatype.FreeTextDatatype\"}";
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		Object locationAttributeType = new LocationAttributeTypeController().create(post, emptyRequest(),
//		    new MockHttpServletResponse());
//		Util.log("Created location attribute type", locationAttributeType);
//		Assert.assertEquals(before + 1, Context.getLocationService().getAllLocationAttributeTypes().size());
//	}
//	
//	/**
//	 * @see
//	 * LocationAttributeTypeController#getLocationAttributeType(LocationAttributeType,WebRequest)
//	 * @verifies get a default representation of a location attribute type
//	 */
//	@Test
//	public void getLocationAttributeType_shouldGetADefaultRepresentationOfALocationAttributeType() throws Exception {
//		Object result = new LocationAttributeTypeController().retrieve(Rest19ExtTestConstants.LOCATION_ATTRIBUTE_TYPE_UUID,
//		    emptyRequest());
//		Assert.assertNotNull(result);
//		Util.log("LocationAttributeType fetched (default)", result);
//		Assert.assertEquals(Rest19ExtTestConstants.LOCATION_ATTRIBUTE_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "name"));
//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
//	}
//	
//	/**
//	 * @see
//	 * LocationAttributeTypeController#getLocationAttributeType(String,WebRequest)
//	 * @verifies get a full representation of a location attribute type
//	 */
//	@Test
//	public void getLocationAttributeType_shouldGetAFullRepresentationOfALocationAttributeType() throws Exception {
//		MockHttpServletRequest req = new MockHttpServletRequest();
//		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
//		Object result = new LocationAttributeTypeController().retrieve(Rest19ExtTestConstants.LOCATION_ATTRIBUTE_TYPE_UUID,
//		    req);
//		Util.log("LocationAttributeType fetched (full)", result);
//		Assert.assertNotNull(result);
//		Assert.assertEquals(Rest19ExtTestConstants.LOCATION_ATTRIBUTE_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
//	}
//	
//	/**
//	 * @see
//	 * LocationAttributeTypeController#updateLocationAttributeType(LocationAttributeType,SimpleObject,WebRequest)
//	 * @verifies change a property on a location
//	 */
//	@Test
//	public void updateLocationAttributeType_shouldChangeAPropertyOnALocationAttributeType() throws Exception {
//		SimpleObject post = new ObjectMapper().readValue("{\"description\":\"Updated description\"}", SimpleObject.class);
//		Object editedLocationAttributeType = new LocationAttributeTypeController().update(
//		    Rest19ExtTestConstants.LOCATION_ATTRIBUTE_TYPE_UUID, post, emptyRequest(), new MockHttpServletResponse());
//		Util.log("Edited location", editedLocationAttributeType);
//		Assert
//		        .assertEquals("Updated description", Context.getLocationService().getLocationAttributeType(1)
//		                .getDescription());
//	}
//	
//	/**
//	 * @see
//	 * LocationAttributeTypeController#retireLocationAttributeType(LocationAttributeType,String,WebRequest)
//	 * @verifies void a location attribute type
//	 */
//	@Test
//	public void retireLocationAttributeType_shouldRetireALocationAttributeType() throws Exception {
//		LocationAttributeType locationAttributeType = Context.getLocationService().getLocationAttributeType(1);
//		Assert.assertFalse(locationAttributeType.isRetired());
//		new LocationAttributeTypeController().delete(Rest19ExtTestConstants.LOCATION_ATTRIBUTE_TYPE_UUID, "test",
//		    emptyRequest(), new MockHttpServletResponse());
//		locationAttributeType = Context.getLocationService().getLocationAttributeType(1);
//		Assert.assertTrue(locationAttributeType.isRetired());
//		Assert.assertEquals("test", locationAttributeType.getRetireReason());
//	}
//	
//	/**
//	 * @see
//	 * LocationAttributeTypeController#findLocationAttributeTypes(String,WebRequest,HttpServletResponse)
//	 * @verifies return no results if there are no matching location(s)
//	 */
//	@Test
//	public void findLocationAttributeTypes_shouldReturnNoResultsIfThereAreNoMatchingLocations() throws Exception {
//		List<Object> results = (List<Object>) new LocationAttributeTypeController().search("zzzznotype", emptyRequest(),
//		    new MockHttpServletResponse()).get("results");
//		Assert.assertEquals(0, results.size());
//	}
//	
//	/**
//	 * @see
//	 * LocationAttributeTypeController#findLocationAttributeTypes(String,WebRequest,HttpServletResponse)
//	 * @verifies find matching location attribute types
//	 */
//	@Test
//	public void findLocationAttributeTypes_shouldFindMatchingLocationAttributeTypes() throws Exception {
//		List<Object> results = (List<Object>) new LocationAttributeTypeController().search("Audit", emptyRequest(),
//		    new MockHttpServletResponse()).get("results");
//		Assert.assertEquals(1, results.size());
//		Util.log("Found " + results.size() + " LocationAttributeType(s)", results);
//		Object result = results.get(0);
//		Assert.assertEquals(Rest19ExtTestConstants.LOCATION_ATTRIBUTE_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
//	}
}
