/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_9;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class ObservationSearchHandlerTest extends RestControllerTestUtils {
	
	protected String getURI() {
		return "obs";
	}
	
	@Test
	public void shouldReturnObsForPatientAndConcept() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("patient", "5946f880-b197-400b-9caa-a3c661d23041");
		req.addParameter("concept", "96408258-000b-424e-af1a-403919332938");
		SimpleObject result = deserialize(handle(req));
		List<Object> obs = result.get("results");
		Assert.assertEquals(1, obs.size());
		Assert.assertEquals("e26cea2c-1b9f-4afe-b211-f3ef6c88af6f", PropertyUtils.getProperty(obs.get(0), "uuid"));
	}
	
	@Test
	public void shouldReturnObsForPatientAndConcepts() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("patient", "5946f880-b197-400b-9caa-a3c661d23041");
		req.addParameter("concepts", "96408258-000b-424e-af1a-403919332938,0dde1358-7fcf-4341-a330-f119241a46e8");
		SimpleObject result = deserialize(handle(req));
		List<Object> obs = result.get("results");
		Assert.assertEquals(2, obs.size());
		// TODO this test shouldn't really be dependent on order
		Assert.assertEquals("e26cea2c-1b9f-4afe-b211-f3ef6c88af6f", PropertyUtils.getProperty(obs.get(0), "uuid"));
		Assert.assertEquals("b6521c32-47b6-47da-9c6f-3673ddfb74f9", PropertyUtils.getProperty(obs.get(1), "uuid"));
	}
}
