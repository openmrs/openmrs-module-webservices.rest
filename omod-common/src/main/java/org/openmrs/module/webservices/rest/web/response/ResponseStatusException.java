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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class ResponseStatusException extends ResourceDoesNotSupportOperationException {

	private static final long serialVersionUID = 1L;

	private final String message;
	private final HttpStatus httpStatus;

	public ResponseStatusException(HttpStatus httpStatus, String message) {
		this.message = message;
		this.httpStatus = httpStatus;
	}

	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<ResponseStatusException> handleException(ObjectNotFoundException e) {
	    ResponseStatusException exception = new ResponseStatusException(HttpStatus.NO_CONTENT, e.getLocalizedMessage());
	    return new ResponseEntity<ResponseStatusException>(exception, null);
	}
}
