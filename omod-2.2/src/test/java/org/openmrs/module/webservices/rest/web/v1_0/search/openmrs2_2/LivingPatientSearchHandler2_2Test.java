/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */


package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs2_2;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;



import java.util.List;


public class LivingPatientSearchHandler2_2Test extends   MainResourceControllerTest {
	
	@Override
	public String getURI() {
		return "patient";
	}

	@Override
	public String getUuid() {
	
		return RestTestConstants1_8.PATIENT_UUID;
	}

	@Override
	public long getAllCount() {
	
		return Context.getPatientService().getAllPatients(false).size();
	}
	
	@Test
	public void getSearchConfig_shouldReturnAllPatients() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("includeDead", "true");
		
		SimpleObject result = deserialize(handle(req));
		List<Object> patients = (List<Object>) result.get("results");
		Assert.assertEquals(4, patients.size());
		
	}
	
	@Test
	public void getSearchConfig_shouldReturnOnlyLiving() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("includeDead", "false");
		
		SimpleObject result = deserialize(handle(req));
		List<Object> patients = (List<Object>) result.get("results");
		Assert.assertEquals(4, patients.size());
		
	}
	
	@Test
	public void getSearchConfig_shouldReturn() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("includeDead", "wrong");
		
		SimpleObject result = deserialize(handle(req));
		List<Object> patients = (List<Object>) result.get("results");
		Assert.assertEquals(4, patients.size());
		
	}
	
	
}
