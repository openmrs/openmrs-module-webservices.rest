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
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.UnknownResourceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Resource controllers should extend this base class to have standard exception handling done automatically.
 * (This is necessary to send error messages as HTTP statuses rather than just as html content, as the
 * core web application does.)
 */
@Controller
@RequestMapping(value = "/rest/**")
public class BaseRestController {
	
	private int errorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	
	private String errorDetail;
	
	@ExceptionHandler(ObjectNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	private SimpleObject objectNotFoundExceptionHandler(Exception ex, HttpServletResponse response) throws Exception {
		errorDetail = ex.getMessage();
		SimpleObject ret = new SimpleObject();
		ret.put("message", "Object not found");
		ret.put("code", HttpStatus.NOT_FOUND);
		ret.put("detail", errorDetail);
		return ret;
	}
	
	@ExceptionHandler(APIException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	private SimpleObject apiExceptionHandler(Exception ex, HttpServletResponse response) throws Exception {
		errorDetail = ex.getMessage();
		SimpleObject ret = new SimpleObject();
		ret.put("message", "OpenMRS API Exception");
		ret.put("code", HttpStatus.BAD_REQUEST);
		ret.put("detail", errorDetail);
		return ret;
	}
	
	@ExceptionHandler(APIAuthenticationException.class)
	@ResponseBody
	private SimpleObject apiAuthenticationExceptionHandler(Exception ex, HttpServletResponse response) throws Exception {
		if (Context.isAuthenticated()) {
			// user is logged in but doesn't have the relevant privilege -> 403 FORBIDDEN
			errorCode = HttpServletResponse.SC_FORBIDDEN;
			errorDetail = "User is logged in but doesn't have the relevant privilege";
		} else {
			// user is not logged in -> 401 UNAUTHORIZED
			errorCode = HttpServletResponse.SC_UNAUTHORIZED;
			errorDetail = "User is not logged in";
			response.addHeader("WWW-Authenticate", "Basic realm=\"OpenMRS at " + RestConstants.URI_PREFIX + "\"");
		}
		response.setStatus(errorCode);
		errorDetail = ex.getMessage();
		SimpleObject ret = new SimpleObject();
		ret.put("message", "APIAuthentication Exception");
		ret.put("code", errorCode);
		ret.put("detail", errorDetail);
		return ret;
	}
	
	@ExceptionHandler(ResourceDoesNotSupportOperationException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	private SimpleObject operationNotSupportedExceptionHandler(Exception ex, HttpServletResponse response) throws Exception {
		errorDetail = ex.getMessage();
		SimpleObject ret = new SimpleObject();
		ret.put("message", "APIAuthentication Exception");
		ret.put("code", errorCode);
		ret.put("detail", errorDetail);
		return ret;
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	private SimpleObject handleException(Exception ex, HttpServletResponse response) throws Exception {
		ResponseStatus ann = ex.getClass().getAnnotation(ResponseStatus.class);
		if (ann != null) {
			errorCode = ann.value().value();
			if (StringUtils.isNotEmpty(ann.reason())) {
				errorDetail = ann.reason();
			}
			if (errorDetail == null) {
				errorDetail = ex.getClass().getName() + ": " + ex.getMessage();
			}
			
		} else if (RestUtil.hasCause(ex, APIAuthenticationException.class)) {
			return apiAuthenticationExceptionHandler(ex, response);
		}
		response.setStatus(errorCode);
		SimpleObject ret = new SimpleObject();
		ret.put("message", "APIAuthentication Exception");
		ret.put("code", errorCode);
		ret.put("detail", errorDetail);
		return ret;
	}
	
	/**
	 * Shows response for unknown resource calls that give default 404
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	private SimpleObject handleUnknownResource() throws Exception {
		return new UnknownResourceException().sendErrorResponse();
	}
}
