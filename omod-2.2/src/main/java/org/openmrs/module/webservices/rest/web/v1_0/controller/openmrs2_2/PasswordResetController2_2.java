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

import org.openmrs.api.InvalidActivationKeyException;
import org.openmrs.api.UserService;
import org.openmrs.api.ValidationException;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.Task.PasswordResetTask;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.notification.MessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Component("ResetPasswordComponent")
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/passwordreset")
public class PasswordResetController2_2 extends BaseRestController  implements DaemonTokenAware {

	@Qualifier("userService")
	@Autowired
	private UserService userService;

	private DaemonToken daemonToken;

	public void setDaemonToken(DaemonToken daemonToken) {
		this.daemonToken = daemonToken;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK, reason = "Please check your email for the reset password link")
	public void requestPasswordReset(@RequestBody Map<String, String> body) {
		String username = body.get("username");
		PasswordResetTask task =new PasswordResetTask(userService,username);
		Daemon.runInDaemonThread(task,daemonToken);
	}
	
	@RequestMapping(value = "/{activationkey}", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void resetPassword(@PathVariable("activationkey") String activationkey,
	        @RequestBody Map<String, String> body) {
		String newPassword = body.get("newPassword");
		try {
			userService.changePasswordUsingActivationKey(activationkey, newPassword);
		}
		catch (InvalidActivationKeyException ex) {
			throw new ValidationException(ex.getMessage());
		}

	}

}
