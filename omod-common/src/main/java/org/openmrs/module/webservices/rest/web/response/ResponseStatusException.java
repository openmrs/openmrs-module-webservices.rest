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

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NO_CONTENT, reason = "No Content Found")
public class ResponseStatusException extends ResponseException {

	private static final long serialVersionUID = 1L;
	
	private Date timestamp;
	private HttpStatus httpStatus;
	private String message;

	public ResponseStatusException() {}

	public ResponseStatusException(Date timestamp, HttpStatus httpStatus, String message) {
		this.timestamp = timestamp;
		this.httpStatus = httpStatus;
		this.message = message;
	}

}
