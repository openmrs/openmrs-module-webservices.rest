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

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.VisitType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VisitsConfigurationController2_0Test extends RestControllerTestUtils {

	private AdministrationService administrationService;

	private VisitService visitService;

	@Before
	public void before() throws SchedulerException {
		administrationService = Context.getAdministrationService();
		visitService = Context.getVisitService();
	}

	@Test
	public void shouldGetCurrentConfiguration() throws Exception {
		// set initial configuration

		// enable visits
		GlobalProperty enableVisitsGp = new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_ENABLE_VISITS, "true");
		administrationService.saveGlobalProperty(enableVisitsGp);

		// set encounter visit handler
		GlobalProperty visitEncounterHandlerGp = new GlobalProperty(OpenmrsConstants.GP_VISIT_ASSIGNMENT_HANDLER,
				"org.openmrs.api.handler.NoVisitAssignmentHandler");
		administrationService.saveGlobalProperty(visitEncounterHandlerGp);

		// set visit types to auto close
		VisitType testVisitType = visitService.getVisitTypeByUuid(RestTestConstants1_9.VISIT_TYPE_UUID);
		GlobalProperty visitTypesToAutoCloseGp = new GlobalProperty(OpenmrsConstants.GP_VISIT_TYPES_TO_AUTO_CLOSE,
				testVisitType.getName());
		administrationService.saveGlobalProperty(visitTypesToAutoCloseGp);

		// make GET call
		MockHttpServletRequest req = request(RequestMethod.GET, "visitconfiguration");
		SimpleObject result = deserialize(handle(req));

		// assert response
		assertNotNull(result);
		assertEquals(true, PropertyUtils.getProperty(result, "enableVisits"));
		assertEquals("org.openmrs.api.handler.NoVisitAssignmentHandler",
				PropertyUtils.getProperty(result, "encounterVisitsAssignmentHandler"));

		List<Object> visitTypesToAutoClose = (List<Object>) PropertyUtils.getProperty(result, "visitTypesToAutoClose");
		assertEquals(1, visitTypesToAutoClose.size());
	}

	@Test
	public void shouldUpdateCurrentConfiguration() throws Exception {
		// update configuration
		String json =
				"{\"enableVisits\": true,\"encounterVisitsAssignmentHandler\": \"org.openmrs.api.handler.NoVisitAssignmentHandler\",\"startAutoCloseVisitsTask\": true,\"visitTypesToAutoClose\":[{\"uuid\":\""
						+ RestTestConstants1_9.VISIT_TYPE_UUID + "\"}]}";
		handle(newPostRequest("visitconfiguration", json));

		// make GET call
		MockHttpServletRequest req = request(RequestMethod.GET, "visitconfiguration");
		SimpleObject result = deserialize(handle(req));

		// assert response
		assertNotNull(result);
		assertEquals(true, PropertyUtils.getProperty(result, "enableVisits"));
		assertEquals("org.openmrs.api.handler.NoVisitAssignmentHandler",
				PropertyUtils.getProperty(result, "encounterVisitsAssignmentHandler"));

		List<Object> visitTypesToAutoClose = (List<Object>) PropertyUtils.getProperty(result, "visitTypesToAutoClose");
		assertEquals(1, visitTypesToAutoClose.size());
	}
}
