/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.Task;

import org.openmrs.User;
import org.openmrs.api.UserService;
import org.openmrs.notification.MessageException;

public class PasswordResetTask implements Runnable {

	private UserService userService;
	
	private String username;
	
	public PasswordResetTask(UserService userService, String username) {
		this.userService = userService;
		this.username = username;
	}
	
	@Override
	public void run() {
		User user = userService.getUserByUsername(username);
		try {
			userService.setUserActivationKey(user);
		}
		catch (MessageException e) {
			throw new RuntimeException(e);
		}
	}
}
