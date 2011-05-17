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
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Interface implemented by resources that support the standard-pattern Purge operation (i.e. a
 * DELETE on the resource, with a purge=true parameter)
 */
public interface Purgeable extends Resource {
	
	/**
	 * Deletes a resources from persistent storage, so that it no longer exists, not even in a
	 * voided or retired state.
	 * 
	 * @param uuid
	 * @param context
	 * @throws ObjectNotFoundException
	 * @throws ResponseException
	 */
	void purge(String uuid, RequestContext context) throws ResponseException;
	
}
