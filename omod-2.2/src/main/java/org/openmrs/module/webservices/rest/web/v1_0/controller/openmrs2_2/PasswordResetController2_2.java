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
import org.openmrs.api.UserService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/passwordreset")
public class PasswordResetController2_2 extends BaseRestController {
	
	@Qualifier("userService")
	@Autowired
	private UserService userService;
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void requestPasswordReset(@RequestBody Map<String, String> body) {
		User user = null;
		String usernameOrEmail = body.get("usernameOrEmail");
		if ((user = userService.getUserByEmail(usernameOrEmail)) == null) {
			user = userService.getUserByUsername(usernameOrEmail);
		}
		//user = userService.getUserByEmail(usernameOrEmail);
		if (user != null) {
			userService.setUserActivationKey(user);
		}
	}
	
	@RequestMapping(value = "/{activationkey}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> resetPassword(@PathVariable("activationkey") String activationkey,
	        @RequestBody Map<String, String> body) {
		String newPassword = body.get("newPassword");
		if (userService.getUserByActivationKey(activationkey) == null) {
			return new ResponseEntity<String>("Invalid Activation Key", HttpStatus.BAD_REQUEST);
		}
		else {
			userService.changePasswordUsingActivationKey(activationkey, newPassword);
			return new ResponseEntity<String>(HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/{activationkey}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> verifyActivationKey(@PathVariable("activationkey") String activationkey) {
		User user = userService.getUserByActivationKey(activationkey);
		if (user == null) {
			return new ResponseEntity<String>("Invalid Activation Key", HttpStatus.BAD_REQUEST);
		}
		else {
			return new ResponseEntity<String>(HttpStatus.OK);
		}
		
	}
	
}
