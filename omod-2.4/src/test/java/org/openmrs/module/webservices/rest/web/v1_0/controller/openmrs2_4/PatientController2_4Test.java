/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_4;

import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.junit.Assert.assertTrue;

public class PatientController2_4Test extends RestControllerTestUtils {

	@Test
	public void shouldReturnDuplicatesPatients() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, "patient");
		req.addParameter("attributesToFindDuplicatesBy", "gender,middleName");

		SimpleObject result = deserialize(handle(req));

		assertTrue(Util.getResultsSize(result) > 0);
	}

}