/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs2_9;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class LocationForCurrentUserSearchHandler2_9Test extends MainResourceControllerTest {
	
	private static final String LOCATION_TAG_INITIAL_XML = "customLocationTagDataset.xml";
	
	/**
	 * Carries both tags below. Location 3 carries them too but is retired, so it never shows up.
	 */
	private static final int FIRST_LOCATION_ID = 1;
	
	/**
	 * Carries WIDE_TAG only.
	 */
	private static final int SECOND_LOCATION_ID = 2;
	
	/**
	 * Tags locations 1, 2 and 3 in the dataset.
	 */
	private static final String WIDE_TAG = "Gastroenteritis infection scandal of 1993";
	
	/**
	 * Tags locations 1 and 3 in the dataset, so location 2 does not carry it.
	 */
	private static final String NARROW_TAG = "General Hospital";
	
	private LocationService service;
	
	@BeforeEach
	public void init() throws Exception {
		service = Context.getLocationService();
		executeDataSet(LOCATION_TAG_INITIAL_XML);
	}
	
	@Override
	public String getURI() {
		return "location";
	}
	
	@Override
	public String getUuid() {
		return RestTestConstants1_8.LOCATION_UUID;
	}
	
	@Override
	public long getAllCount() {
		return service.getAllLocations(false).size();
	}
	
	@Test
	public void search_shouldReturnAllTaggedLocationsWhenTheUserHasNoAssignedLocations() throws Exception {
		assertEquals(2, searchForCurrentUser("true", WIDE_TAG).size());
	}
	
	@Test
	public void search_shouldReturnOnlyTheAssignedLocationsThatCarryTheTag() throws Exception {
		assignLocations(FIRST_LOCATION_ID);
		
		List<Object> results = searchForCurrentUser("true", WIDE_TAG);
		
		assertEquals(1, results.size());
		assertEquals(service.getLocation(FIRST_LOCATION_ID).getUuid(),
		    PropertyUtils.getProperty(results.get(0), "uuid"));
	}
	
	@Test
	public void search_shouldReturnAnEmptyListWhenNoAssignedLocationCarriesTheTag() throws Exception {
		assignLocations(SECOND_LOCATION_ID);
		
		assertEquals(0, searchForCurrentUser("true", NARROW_TAG).size());
	}
	
	@Test
	public void search_shouldIgnoreAssignedLocationsWhenForCurrentUserIsFalse() throws Exception {
		assignLocations(FIRST_LOCATION_ID);
		
		assertEquals(2, searchForCurrentUser("false", WIDE_TAG).size());
	}
	
	private List<Object> searchForCurrentUser(String forCurrentUser, String tag) throws Exception {
		MockHttpServletRequest request = request(RequestMethod.GET, getURI());
		request.addParameter("forCurrentUser", forCurrentUser);
		request.addParameter("tag", tag);
		
		SimpleObject result = deserialize(handle(request));
		return result.get("results");
	}
	
	private void assignLocations(Integer... locationIds) {
		Set<Location> locations = new HashSet<Location>();
		for (Integer locationId : locationIds) {
			locations.add(service.getLocation(locationId));
		}
		
		User user = Context.getAuthenticatedUser();
		user.setAssignedLocations(locations);
		Context.getUserService().saveUser(user);
	}
}
