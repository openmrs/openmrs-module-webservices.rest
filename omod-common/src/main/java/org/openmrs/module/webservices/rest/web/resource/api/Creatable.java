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

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Interface implemented by resources that have the standard-pattern Create operation
 */
public interface Creatable extends Resource {
	
	/**
	 * Creates a resource (presumably saving it to some persistent storage) and returns the resource
	 * created
	 * 
	 * @param post desc needed
	 * @param context desc needed
	 * @return desc needed
	 * @throws ResponseException desc needed
	 */
	public Object create(SimpleObject post, RequestContext context) throws ResponseException;
	
}
