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
 * This exception should be thrown from controllers when passed a uuid that does not represent an
 * existing resource.
 */
public class ObjectNotFoundException extends ResponseException {
	
	private static final long serialVersionUID = 1L;
	
	public String reason = "Object with given uuid doesn't exist";
	
	public String code = "404";
	
	public String detail = "You have requested a uuid that is not available in the database. Use q parameter to query using a String";
	
	public ObjectNotFoundException() {
		super.reason = reason;
		super.code = code;
		super.detail = detail;
	}
}
