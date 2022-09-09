/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception should be thrown from controllers when a resource is fetched using a uuid(
 * for example uuid of a patient)but the resource being fetched is empty or unknown (like allergy).
 */
@ResponseStatus(value = HttpStatus.NO_CONTENT, reason = "Object with given uuid is empty")
public class NoContentFoundException extends ResponseException {
	private static final long serialVersionUID = 1L;
	
	public NoContentFoundException() {
		
	}
	
	public NoContentFoundException(String message) {
		super(message);
	}
}
