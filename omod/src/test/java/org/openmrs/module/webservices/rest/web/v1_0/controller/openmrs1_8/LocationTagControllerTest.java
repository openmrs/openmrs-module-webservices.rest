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

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests functionality of {@link LocationTagController}. This does not use @should annotations because
 * the controller inherits those methods from a subclass
 */
public class LocationTagControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	String idTypeUuid = "60cedb47-88bb-11e1-b45c-0024e8c61285";
	//	
	//	private LocationService service;
	//	
	//	private LocationTagController controller;
	//	
	//	private MockHttpServletRequest request;
	//	
	//	private HttpServletResponse response;
	//	
	//	@Before
	//	public void before() throws Exception {
	//		executeDataSet("loctag_data.xml");
	//		this.service = Context.getLocationService();
	//		this.controller = new LocationTagController();
	//		this.request = new MockHttpServletRequest();
	//		this.response = new MockHttpServletResponse();
	//	}
	//	
	//	@Test
	//	public void shouldCreate() throws Exception {
	//		int before = service.getAllLocationTags().size();
	//		String json = "{ \"name\":\"My Type\", \"description\":\"My Way\"}";
	//		SimpleObject post = SimpleObject.parseJson(json);
	//		controller.create(post, request, response);
	//		int after = service.getAllLocationTags().size();
	//		Assert.assertEquals(before + 1, after);
	//	}
	//	
	//	@Test
	//	public void shouldGetOne() throws Exception {
	//		SimpleObject result = (SimpleObject) controller.retrieve(idTypeUuid, request);
	//		Assert.assertNotNull(result);
	//		//Util.log("Location Tag fetched (default)", result);
	//		Assert.assertEquals("Laboratory", PropertyUtils.getProperty(result, "name"));
	//		//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	@Test
	//	public void shouldListAll() throws Exception {
	//		SimpleObject result = controller.getAll(request, response);
	//		//Util.log("All non-retired location tags", result);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals(1, result.size());
	//	}
	//	
	//	@Test
	//	public void shouldUpdate() throws Exception {
	//		String json = "{ \"description\":\"something new\" }";
	//		SimpleObject post = SimpleObject.parseJson(json);
	//		controller.update(idTypeUuid, post, request, response);
	//		LocationTag updated = service.getLocationTagByUuid(idTypeUuid);
	//		//Util.log("Updated", updated);
	//		Assert.assertNotNull(updated);
	//		Assert.assertEquals("something new", updated.getDescription());
	//	}
	//	
	//	@Test(expected = Exception.class)
	//	// should fail to purge an item referenced by other data
	//	public void shouldFailToPurge() throws Exception {
	//		Number nbefore = (Number) Context.getAdministrationService().executeSQL("select count(*) from location_tag", true)
	//		        .get(0).get(0);
	//		controller.purge(idTypeUuid, request, response);
	//		Context.flushSession();
	//		Number nafter = (Number) Context.getAdministrationService().executeSQL("select count(*) from location_tag", true)
	//		        .get(0).get(0);
	//		Assert.assertEquals(nbefore.intValue() - 1, nafter.intValue());
	//	}
	//	
	//	@Test
	//	public void shouldPurge() throws Exception {
	//		String json = "{ \"name\":\"My Type 2\", \"description\":\"My Way 2\"}";
	//		SimpleObject post = SimpleObject.parseJson(json);
	//		SimpleObject created = (SimpleObject) controller.create(post, request, response);
	//		String uuid = PropertyUtils.getProperty(created, "uuid").toString();
	//		LocationTag idType = service.getLocationTagByUuid(uuid);
	//		Assert.assertFalse(idType.isRetired());
	//		controller.delete(uuid, "unit test", request, response);
	//		idType = service.getLocationTagByUuid(uuid);
	//		Assert.assertTrue(idType.isRetired());
	//		Assert.assertEquals("unit test", idType.getRetireReason());
	//		controller.purge(uuid, request, response);
	//		idType = service.getLocationTagByUuid(uuid);
	//		Assert.assertNull(idType);
	//	}
	//	
	//	@Test
	//	public void shouldDelete() throws Exception {
	//		LocationTag idType = service.getLocationTagByUuid(idTypeUuid);
	//		Assert.assertFalse(idType.isRetired());
	//		controller.delete(idTypeUuid, "unit test", request, response);
	//		idType = service.getLocationTagByUuid(idTypeUuid);
	//		Assert.assertTrue(idType.isRetired());
	//		Assert.assertEquals("unit test", idType.getRetireReason());
	//	}
	
	/*	

	*/

	@Test
	public void fakeTest() {
		
	}
}
