/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_9;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class UserController2_9Test extends MainResourceControllerTest {
	
	private UserService service;
	
	@BeforeEach
	public void init() {
		service = Context.getUserService();
	}
	
	@Override
	public String getURI() {
		return "user";
	}
	
	@Override
	public String getUuid() {
		return RestTestConstants1_8.USER_UUID;
	}
	
	@Override
	public long getAllCount() {
		return service.getAllUsers().size();
	}
	
	@Test
	public void updateUser_shouldAssignLocationsToTheUser() throws Exception {
		Location location = Context.getLocationService().getLocation(1);
		
		assignLocation(location);
		
		User edited = service.getUserByUuid(getUuid());
		assertEquals(1, edited.getAssignedLocations().size());
		assertTrue(edited.getAssignedLocations().contains(location));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getUser_shouldIncludeAssignedLocationsInTheFullRepresentation() throws Exception {
		Location location = Context.getLocationService().getLocation(1);
		assignLocation(location);
		
		MockHttpServletRequest request = request(RequestMethod.GET, getURI() + "/" + getUuid());
		request.addParameter("v", "full");
		
		SimpleObject result = deserialize(handle(request));
		List<Object> locations = (List<Object>) PropertyUtils.getProperty(result, "locations");
		
		assertNotNull(locations);
		assertEquals(1, locations.size());
		assertEquals(location.getUuid(), PropertyUtils.getProperty(locations.get(0), "uuid"));
	}
	
	private void assignLocation(Location location) throws Exception {
		MockHttpServletRequest request = request(RequestMethod.POST, getURI() + "/" + getUuid());
		request.setContent(("{\"locations\":[\"" + location.getUuid() + "\"]}").getBytes());
		handle(request);
	}
}
