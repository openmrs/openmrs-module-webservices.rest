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

import java.util.Map;

import org.openmrs.User;
import org.openmrs.api.InvalidActivationKeyException;
import org.openmrs.api.UserService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1)
public class PasswordResetController extends BaseRestController {
	
	@Qualifier("userService")
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/requestpasswordreset", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void requestPasswordReset(@RequestBody Map<String, String> body) {
		User user = null;
		
		if (body.containsKey("email")) {
			String email = body.get("email");
			user = userService.getUserByEmail(email);
		}
		else if (body.containsKey("username")) {
			String userName = body.get("username");
			user = userService.getUserByUsername(userName);
		}
		
		if (user != null) {
			userService.setUserActivationKey(user);
		}
	}
	
	@RequestMapping(value = "/resetpasswordrequest", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void resetPassword(@RequestBody Map<String, String> body) {
		String newPassword = body.get("newPassword");
		
		String token = body.get("token");
		
		try {
			userService.changeUserPasswordUsingActivationKey(token, newPassword);
		}
		catch (InvalidActivationKeyException ex) {
			ex.printStackTrace();
		}
		
	}
	
}
