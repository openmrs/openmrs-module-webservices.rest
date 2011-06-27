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
package org.openmrs.module.webservices.rest.web.response;

import java.util.LinkedHashMap;
import org.openmrs.module.webservices.rest.SimpleObject;

/**
 * This is a marker class for all response exceptions. Controllers should throw
 * these if an error has occurred or a status needs to be shown to a user
 */
public abstract class ResponseException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	protected String reason;
	
	protected String code;
	
	protected String detail;
	
	public ResponseException() {
		super();
		sendErrorResponse();
	}
	
	public ResponseException(String message, Throwable cause) {
		super(message, cause);
		sendErrorResponse();
	}
	
	public SimpleObject sendErrorResponse() {
		SimpleObject err = new SimpleObject();
		LinkedHashMap map = new LinkedHashMap();
		map.put("message", this.reason);
		map.put("code", this.code);
		map.put("detail", this.detail);
		err.add("error", map);
		return err;
	}
}
