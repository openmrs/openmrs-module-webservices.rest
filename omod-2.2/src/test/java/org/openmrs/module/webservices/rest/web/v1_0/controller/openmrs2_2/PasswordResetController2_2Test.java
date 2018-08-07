/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_2;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletResponse;

public class PasswordResetController2_2Test extends RestControllerTestUtils {
	
	private static final String RESET_PASSWORD_URI = "passwordreset";
	
	private MessageSourceService messages;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Autowired
	@Qualifier("userService")
	private UserService userService;
	
	@Before
	public void setup() {
		userService = Context.getUserService();
		messages = Context.getMessageSourceService();
	}
	
	@Test
	public void requestPasswordReset_shouldFailIfUsernameOrEmailIsBlanck() throws Exception {
		User u = new User();
		u.setPerson(new Person());
		u.addName(new PersonName("Benjamin", "A", "Wolfe"));
		u.setUsername("bwolfe");
		u.getPerson().setGender("M");
		userService.createUser(u, "Openmr5xy");
		String usernameOrEmail = "  ";
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage(messages.getMessage("error.email.notNullOrBlank"));
		handle(newPostRequest(RESET_PASSWORD_URI, "{\"usernameOrEmail\":\"" + usernameOrEmail + "\"}"));
		
	}
	
	@Test
	public void requestPasswordReset_shouldFailIfUsernameOrEmailIsEmptyl() throws Exception {
		User u = new User();
		u.setPerson(new Person());
		u.addName(new PersonName("Benjamin", "A", "Wolfe"));
		u.setUsername("bwolfe");
		u.getPerson().setGender("M");
		userService.createUser(u, "Openmr5xy");
		String usernameOrEmail = "";
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage(messages.getMessage("error.email.notNullOrBlank"));
		handle(newPostRequest(RESET_PASSWORD_URI, "{\"usernameOrEmail\":\"" + usernameOrEmail + "\"}"));
		
	}
	
	@Test
	public void verifyActivationKey_shouldFailWith400BadRequestIfActivationKeyIsInvalid() throws Exception {
		String activationKey = "wrongActivationKey12";
		MockHttpServletResponse response = handle(newGetRequest(RESET_PASSWORD_URI + "/" + activationKey));
		assertEquals(400, response.getStatus());
	}
	
	@Test
	public void resetPassword_shouldFailWith400BadRequestIfActivationKeyIsInvalid() throws Exception {
		String activationkey = "wrongActivationKey12";
		String newPassword = "newPassword9";
		MockHttpServletResponse response = handle(newPostRequest(RESET_PASSWORD_URI + "/" + activationkey,
		    "{\"newPassword\":\"" + newPassword + "\"}"));
		assertEquals(400, response.getStatus());
		
	}
}
