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
package org.openmrs.module.webservices.rest.web.resource.api;

import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Interface implemented by resources that support the standard-pattern Delete operation
 */
public interface Deletable extends Resource {
	
	/**
	 * Deletes the specified resource, which in the OpenMRS context means either voiding or retiring
	 * it
	 * 
	 * @param uuid
	 * @param reason
	 * @param context
	 * @throws ResponseException
	 */
	void delete(String uuid, String reason, RequestContext context) throws ResponseException;
	
}
