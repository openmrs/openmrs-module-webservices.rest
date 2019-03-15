/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */


package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_11;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;



import java.util.List;


public class LivingPatientSearchHandler1_11Test extends  RestControllerTestUtils  {

	protected String getURI() {
		return "patient";
	}

	
	@Test
	public void getSearchConfig_shouldReturnAllPatients() throws Exception {
		Context.getPatientService().getPatientByUuid(RestTestConstants1_8.PATIENT_UUID).setDead(true);
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("includeDead", "true");	
		SimpleObject result = deserialize(handle(req));
		List<Object> patients =  result.get("results");
		Assert.assertEquals(4, patients.size());
		
	}
	
	
	@Test
	public void getSearchConfig_shouldReturnOnlyLiving() throws Exception {
		Context.getPatientService().getPatientByUuid(RestTestConstants1_8.PATIENT_UUID).setDead(true);
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("includeDead", "false");
		
		SimpleObject result = deserialize(handle(req));
		List<Object> patients =  result.get("results");
		Assert.assertEquals(3, patients.size());
		
	}
	
	@Test
	public void getSearchConfig_shouldReturnOnlyLivingPatients_OnWrongParameterValue() throws Exception {
		Context.getPatientService().getPatientByUuid(RestTestConstants1_8.PATIENT_UUID).setDead(true);
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("includeDead", "wrongx");
		
		SimpleObject result = deserialize(handle(req));
		List<Object> patients =  result.get("results");
		Assert.assertEquals(3, patients.size());
		
	}
	
	
}
