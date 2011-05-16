/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Resource controllers should extend this base class to have standard exception handling done automatically.
 * (This is necessary to send error messages as HTTP statuses rather than just as html content, as the
 * core web application does.)
 */
public abstract class BaseRestController {

	@ExceptionHandler(Exception.class)
	public void handleException(Exception ex, HttpServletResponse response) throws Exception {
		ResponseStatus ann = ex.getClass().getAnnotation(ResponseStatus.class);
		int errorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		String errorDetail = null;
		if (ann != null) {
			errorCode = ann.value().value();
			if (StringUtils.isNotEmpty(ann.reason()))
				errorDetail = ann.reason();
		}
		else if (ex instanceof APIException) {
			// TODO how do we determine whether it's client error or server error? For now we always assume client error
			errorCode = HttpServletResponse.SC_BAD_REQUEST;
		}
		if (errorDetail == null) {
			errorDetail = ex.getClass().getName() + ": " + ex.getMessage();
		}
		if (ex instanceof APIAuthenticationException) {
			if (Context.isAuthenticated()) {
				// user is logged in but doesn't have the relevant privilege -> 403 FORBIDDEN
				errorCode = HttpServletResponse.SC_FORBIDDEN;
			} else {
				// user is not logged in -> 401 UNAUTHORIZED
				// TODO specify authentication mechanism
				errorCode = HttpServletResponse.SC_UNAUTHORIZED;
			}
		}
		response.sendError(errorCode, errorDetail);
	}

}
