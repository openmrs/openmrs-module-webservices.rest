/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.filter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.springframework.mock.web.MockHttpServletResponse;

public class RequestFilterTest extends RestControllerTestUtils {
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void testCORS() throws Exception {
		String uri = getURI() + "/" + getUuid();
		MockHttpServletResponse response = handle(newGetRequest(uri));
		Assert.assertTrue(response.containsHeader("Access-Control-Allow-Origin"));
	}
	
	public String getURI() {
		return "user";
	}
	
	public String getUuid() {
		return Context.getAuthenticatedUser().getUuid();
	}
	
	public long getAllCount() {
		return 1;
	}
}
