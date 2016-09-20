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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception should be thrown from controllers after a purge request that has finished
 * successfully. (Retiring or voiding should return the default representation of the voided/retired
 * resource.)
 */
@ResponseStatus(value = HttpStatus.NO_CONTENT, reason = "Object deleted successfully")
public class SuccessfulDeletion extends ResponseException {
	
	private static final long serialVersionUID = 1L;
	
}
