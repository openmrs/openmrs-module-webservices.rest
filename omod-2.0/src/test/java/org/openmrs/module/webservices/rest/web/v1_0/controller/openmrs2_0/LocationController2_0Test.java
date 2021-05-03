/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_0;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class LocationController2_0Test extends MainResourceControllerTest {
	@Before
	public void init() throws Exception {
		executeDataSet("locationTestDataset.xml");
	}

	@Override
	public String getURI() {
		return "location";
	}

	@Override
	public String getUuid() {
		return "43ac5109-7d8c-11e1-909d-c80aa9edcf4eE";
	}

	@Override
	public long getAllCount() {
		return 6;
	}

	@Test
	public void shouldNotReturnLocationsIfParentLocationIsEmpty() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("parentLocation", "");
		SimpleObject result = deserialize(handle(req));
		List<Object> location = result.get("results");
		Assert.assertEquals(0, location.size());
	}

	@Test
	public void shouldNotReturnLocationsIfParentLocationIsNotSet() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		List<Object> location = result.get("results");
		Assert.assertEquals(6, location.size());
	}

	@Test
	public void shouldNotReturnLocationsIfNoLocationMatchesParentLocation() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("parentLocation", "e4f7be24-7601-449e-b216-8a22d80e11c");
		SimpleObject result = deserialize(handle(req));
		List<Object> location = result.get("results");
		Assert.assertEquals(0, location.size());
	}

	@Test
	public void shouldReturnLocationsIfLocationMatchesParentLocation() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("parentLocation", "f08ba64b-ea57-4a41-nfdr-9dfc59b0c60aA");
		SimpleObject result = deserialize(handle(req));
		List<Object> location = result.get("results");
		Assert.assertEquals(5, location.size());
	}
}
