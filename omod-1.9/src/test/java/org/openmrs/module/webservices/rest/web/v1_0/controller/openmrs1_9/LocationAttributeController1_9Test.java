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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.LocationAttribute;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests functionality of {@link LocationAttributeController}.
 */
public class LocationAttributeController1_9Test extends MainResourceControllerTest {
	
	private LocationService service;
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "location/" + RestTestConstants1_9.LOCATION_UUID + "/attribute";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_9.LOCATION_ATTRIBUTE_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return service.getLocationByUuid(RestTestConstants1_9.LOCATION_UUID).getActiveAttributes().size();
	}
	
	@Before
	public void before() throws Exception {
		executeDataSet(RestTestConstants1_9.TEST_DATASET);
		this.service = Context.getLocationService();
	}
	
	@Test
	public void shouldAddAttributeToLocation() throws Exception {
		int before = service.getLocationByUuid(RestTestConstants1_9.LOCATION_UUID).getAttributes().size();
		String json = "{\"attributeType\":\"9516cc50-6f9f-132r-5433-001e378eb67f\", \"value\":\"2012-05-05\"}";
		handle(newPostRequest(getURI(), json));
		int after = service.getLocationByUuid(RestTestConstants1_9.LOCATION_UUID).getAttributes().size();
		Assert.assertEquals(before + 1, after);
	}
	
	@Test
	public void shouldEditLocationAttribute() throws Exception {
		String json = "{ \"attributeType\":\"9516cc50-6f9f-132r-5433-001e378eb67f\" }";
		
		LocationAttribute locationAttribute = service.getLocationAttributeByUuid(getUuid());
		Assert.assertEquals("Audit Date", locationAttribute.getAttributeType().getName());
		
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		
		locationAttribute = service.getLocationAttributeByUuid(getUuid());
		Assert.assertEquals("Care Date", locationAttribute.getAttributeType().getName());
	}
	
	@Test
	public void shouldVoidAttribute() throws Exception {
		LocationAttribute locationAttribute = service.getLocationAttributeByUuid(getUuid());
		Assert.assertFalse(locationAttribute.isVoided());
		
		MockHttpServletRequest request = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		request.addParameter("reason", "unit test");
		handle(request);
		
		locationAttribute = service.getLocationAttributeByUuid(getUuid());
		Assert.assertTrue(locationAttribute.isVoided());
		Assert.assertEquals("unit test", locationAttribute.getVoidReason());
	}
}
