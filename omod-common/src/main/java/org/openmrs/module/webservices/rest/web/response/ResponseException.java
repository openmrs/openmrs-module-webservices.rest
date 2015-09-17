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

/**
 * This is a marker class for all response exceptions. Controllers should throw these if an error
 * has occurred or a status needs to be shown to a user
 */
public abstract class ResponseException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public ResponseException() {
		super();
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public ResponseException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * @param message
	 */
	public ResponseException(String message) {
		super(message);
	}
	
	/**
	 * @param cause
	 */
	public ResponseException(Throwable cause) {
		super(cause);
	}
	
}
