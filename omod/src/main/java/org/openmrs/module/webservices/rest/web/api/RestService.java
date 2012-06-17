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
package org.openmrs.module.webservices.rest.web.api;

import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;

/**
 * Database related methods for the Rest Web Services
 */
public interface RestService {
	
	/**
	 * Parses a representation requested by the client via the http request
	 * @param requested
	 * @return
	 * @should get ref representation when specified
	 * @should get default representation when specified
	 * @should get full representation when specified
	 * @should get a named representation when specified
	 */
	public Representation getRepresentation(String requested);
	
	/**
	 * Gets an instance of a resource, possibly instantiating a new one, possibly re-using an existing one.
	 * @param <T>
	 * @param resourceClass
	 * @return
	 */
	public <R extends Resource> R getResource(Class<R> resourceClass);
	
}
