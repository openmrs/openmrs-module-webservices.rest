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

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests functionality of {@link LocationController}. This does not use @should annotations because
 * the controller inherits those methods from a subclass
 */
public class LocationControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	private LocationService service;
	//	
	//	private LocationController controller;
	//	
	//	private MockHttpServletRequest request;
	//	
	//	private HttpServletResponse response;
	//	
	//	@Before
	//	public void before() {
	//		this.service = Context.getLocationService();
	//		this.controller = new LocationController();
	//		this.request = new MockHttpServletRequest();
	//		this.response = new MockHttpServletResponse();
	//	}
	//	
	//	@Test
	//	public void shouldGetALocationByUuid() throws Exception {
	//		Object result = controller.retrieve("167ce20c-4785-4285-9119-d197268f7f4a", request);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals("167ce20c-4785-4285-9119-d197268f7f4a", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertEquals("Never Never Land", PropertyUtils.getProperty(result, "name"));
	//	}
	//	
	//	@Test
	//	public void shouldGetALocationByName() throws Exception {
	//		Object result = controller.retrieve("Never Never Land", request);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals("167ce20c-4785-4285-9119-d197268f7f4a", PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertEquals("Never Never Land", PropertyUtils.getProperty(result, "name"));
	//	}
	//	
	//	@Test
	//	public void shouldListAllUnRetiredLocations() throws Exception {
	//		SimpleObject result = controller.getAll(request, response);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals(2, Util.getResultsSize(result));
	//	}
	//	
	//	@Test
	//	public void shouldCreateALocation() throws Exception {
	//		int originalCount = service.getAllLocations().size();
	//		String json = "{ \"name\":\"test location\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object newLocation = controller.create(post, request, response);
	//		Assert.assertNotNull(PropertyUtils.getProperty(newLocation, "uuid"));
	//		Assert.assertEquals(originalCount + 1, service.getAllLocations().size());
	//	}
	//	
	//	@Test
	//	public void shouldEditALocation() throws Exception {
	//		String json = "{ \"address1\":\"new Address1\", \"name\":\"new name\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		controller.update("167ce20c-4785-4285-9119-d197268f7f4a", post, request, response);
	//		Location updated = service.getLocationByUuid("167ce20c-4785-4285-9119-d197268f7f4a");
	//		Assert.assertNotNull(updated);
	//		Assert.assertEquals("new name", updated.getName());
	//		Assert.assertEquals("new Address1", updated.getAddress1());
	//	}
	//	
	//	@Test
	//	public void shouldOverwriteAListOfChildLocations() throws Exception {
	//		Location location = service.getLocationByUuid("167ce20c-4785-4285-9119-d197268f7f4a");
	//		location.addChildLocation(service.getLocationByUuid("39fb7f47-e80a-4056-9285-bd798be13c63"));
	//		service.saveLocation(location);
	//		
	//		String json = "{ \"childLocations\": [] }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		controller.update("167ce20c-4785-4285-9119-d197268f7f4a", post, request, response);
	//		
	//		Location updated = service.getLocationByUuid("167ce20c-4785-4285-9119-d197268f7f4a");
	//		Assert.assertTrue(updated.getChildLocations().isEmpty());
	//	}
	//	
	//	@Test
	//	public void shouldRetireALocation() throws Exception {
	//		String uuid = "dc5c1fcc-0459-4201-bf70-0b90535ba362";
	//		Location location = service.getLocationByUuid(uuid);
	//		Assert.assertFalse(location.isRetired());
	//		controller.delete(uuid, "test reason", request, response);
	//		location = service.getLocationByUuid(uuid);
	//		Assert.assertTrue(location.isRetired());
	//		Assert.assertEquals("test reason", location.getRetireReason());
	//	}
	//	
	//	@Test
	//	public void shouldPurgeALocation() throws Exception {
	//		//All the locations in the test dataset are already in use, so we need to
	//		//create one that we can purge for testing purposes
	//		Location location = new Location();
	//		location.setName("new test location");
	//		service.saveLocation(location);
	//		Assert.assertNotNull(location.getUuid());//should have been inserted for the test to be valid
	//		int originalCount = service.getAllLocations().size();
	//		String uuid = location.getUuid();
	//		controller.purge(uuid, request, response);
	//		Assert.assertNull(service.getLocationByUuid(uuid));
	//		Assert.assertEquals(originalCount - 1, service.getAllLocations().size());
	//	}
	//	
	//	@Test
	//	public void shouldIncludeTheParentLocation() throws Exception {
	//		//Set the parent Location for testing purposes
	//		String uuid = "167ce20c-4785-4285-9119-d197268f7f4a";
	//		Location location = service.getLocationByUuid(uuid);
	//		location.setParentLocation(service.getLocation(2));
	//		service.saveLocation(location);
	//		MockHttpServletRequest httpReq = new MockHttpServletRequest();
	//		httpReq.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//		
	//		Object result = controller.retrieve(uuid, httpReq);
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "parentLocation"));
	//	}
	//	
	//	@Test
	//	public void shouldIncludeTheListOfChildLocations() throws Exception {
	//		//Add a child Location for testing purposes
	//		String parentUuid = "dc5c1fcc-0459-4201-bf70-0b90535ba362";
	//		Location location = service.getLocationByUuid(parentUuid);
	//		Assert.assertEquals(0, location.getChildLocations().size());
	//		location.addChildLocation(service.getLocation(3));
	//		service.saveLocation(location);
	//		MockHttpServletRequest httpReq = new MockHttpServletRequest();
	//		httpReq.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//		
	//		Object result = controller.retrieve(parentUuid, httpReq);
	//		Assert.assertEquals(1, ((Collection) PropertyUtils.getProperty(result, "childLocations")).size());
	//	}
	//	
	//	@Test
	//	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
	//		MockHttpServletRequest httpReq = new MockHttpServletRequest();
	//		httpReq.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
	//		Object result = controller.retrieve("167ce20c-4785-4285-9119-d197268f7f4a", httpReq);
	//		Assert.assertNotNull(result);
	//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	@Test
	//	public void shouldSearchAndReturnAListOfLocationsMatchingTheQueryString() throws Exception {
	//		List<Object> hits = (List<Object>) controller.search("xan", request, response).get("results");
	//		Assert.assertEquals(1, hits.size());
	//		Assert.assertEquals("9356400c-a5a2-4532-8f2b-2361b3446eb8", PropertyUtils.getProperty(hits.get(0), "uuid"));
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
