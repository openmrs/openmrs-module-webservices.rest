/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.api.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.User;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class RestHelperServiceImplTest extends BaseModuleWebContextSensitiveTest {
	
	private static final String USER_SET = "restHelperServiceImplTestDataSet.xml";

	@Autowired
	private RestHelperService restHelperService;

	@Before
	public void setUp() throws Exception {
		executeDataSet(USER_SET);
	}
	 @Test
	public void getUserByUsernameOrEmail_shouldGetUser_ByUsername() {
		String username = "james";
		User user = restHelperService.getUserByUsernameOrEmail(username);
		assertNotNull(user);
		assertEquals(username, user.getUsername());
		assertEquals("c0092c57-fa6d-4a19-b502-3042f57f2379", user.getUuid());
	}

	@Test
	public void getUserByUsernameOrEmail_shouldGetUser_ByEmail() {
		String email = "jamespeter@test.com";
		User user = restHelperService.getUserByUsernameOrEmail(email);
		assertNotNull(user);
		assertEquals("jamespeter@test.com", user.getEmail());
		assertEquals("c0092c57-fa6d-4a19-b502-3042f57f2379", user.getUuid());
		assertFalse(user.getRetired());
	}

	@Test
	public void getUserByUsernameOrEmail_WithInvalidEmail() {
		String INVALID_EMAIL = "user@test.com";
		User user = restHelperService.getUserByUsernameOrEmail(INVALID_EMAIL);
		assertNull(user);
	}

}
