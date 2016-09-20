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

import java.util.List;

import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Interface implemented by resources that have the standard-pattern Retrieve operation by UUID
 */
public interface Retrievable extends Resource {
	
	/**
	 * Gets the object with the given uuid, in the given representation
	 * 
	 * @param uuid
	 * @param context
	 * @return
	 * @throws ResponseException
	 */
	public Object retrieve(String uuid, RequestContext context) throws ResponseException;
	
	/**
	 * @return all representations under which resource can be retrieved
	 */
	List<Representation> getAvailableRepresentations();
	
}
