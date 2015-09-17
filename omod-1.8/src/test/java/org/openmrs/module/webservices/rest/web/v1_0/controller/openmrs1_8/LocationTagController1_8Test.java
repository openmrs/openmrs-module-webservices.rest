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
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests functionality of {@link LocationTagController}. 
 */
public class LocationTagController1_8Test extends MainResourceControllerTest {
	
	private LocationService service;
	
	private static final String LOCATION_TAG_INITIAL_XML = "customLocationTagDataset.xml";
	
	@Before
	public void init() throws Exception {
		service = Context.getLocationService();
		executeDataSet(LOCATION_TAG_INITIAL_XML);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "locationtag";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return service.getAllLocationTags(false).size();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_8.LOCATION_TAG_UUID;
	}
	
	@Test
	public void shouldCreateALocationTag() throws Exception {
		
		long originalCount = getAllCount();
		
		SimpleObject locationTag = new SimpleObject();
		locationTag.add("name", "Location Tag name");
		locationTag.add("description", "Location Tag description");
		
		String json = new ObjectMapper().writeValueAsString(locationTag);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		SimpleObject newLocationTag = deserialize(handle(req));
		
		Assert.assertNotNull(PropertyUtils.getProperty(newLocationTag, "uuid"));
		Assert.assertEquals(originalCount + 1, getAllCount());
		
	}
	
	@Test
	public void shouldGetALocationTagByUuid() throws Exception {
		
		LocationTag locationTag = service.getLocationTag(1);
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + locationTag.getUuid());
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Assert.assertEquals(locationTag.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals(locationTag.getName(), PropertyUtils.getProperty(result, "name"));
		
	}
	
	@Test
	public void shouldListAll() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Assert.assertEquals(getAllCount(), Util.getResultsSize(result));
		
	}
	
	@Test
	public void shouldUpdateLocationTag() throws Exception {
		
		final String editedName = "Location Tag edited";
		String json = "{ \"name\":\"" + editedName + "\" }";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/" + getUuid());
		req.setContent(json.getBytes());
		handle(req);
		LocationTag editedLocationTag = service.getLocationTagByUuid(getUuid());
		Assert.assertNotNull(editedLocationTag);
		Assert.assertEquals(editedName, editedLocationTag.getName());
		
	}
	
	@Test
	public void shouldPurgeLocationTag() throws Exception {
		
		LocationTag locationTag = service.getLocationTag(3);
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + locationTag.getUuid());
		req.addParameter("purge", "");
		handle(req);
		
		Assert.assertNull(service.getLocationTag(3));
	}
	
	@Test
	public void shouldRetireLocationTag() throws Exception {
		
		LocationTag locationTag = service.getLocationTag(2);
		Assert.assertFalse(locationTag.isRetired());
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + locationTag.getUuid());
		req.addParameter("!purge", "");
		req.addParameter("reason", "random reason");
		handle(req);
		
		LocationTag retiredLocationTag = service.getLocationTag(2);
		Assert.assertTrue(retiredLocationTag.isRetired());
		Assert.assertEquals("random reason", retiredLocationTag.getRetireReason());
		
	}
	
}
