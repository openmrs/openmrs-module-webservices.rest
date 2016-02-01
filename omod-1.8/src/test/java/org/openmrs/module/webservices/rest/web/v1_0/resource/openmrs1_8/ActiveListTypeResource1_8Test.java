/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.api.OrderService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class ActiveListTypeResource1_8Test extends BaseModuleWebContextSensitiveTest {

	@Autowired
	private MainResourceController mainResourceController;

	@Mock
	private OrderService orderService;

	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void testResourceDoesNotSupportOperationException() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		SimpleObject activeListTypes = mainResourceController.get("activelisttype", new MockHttpServletRequest(), response);
	}
}
