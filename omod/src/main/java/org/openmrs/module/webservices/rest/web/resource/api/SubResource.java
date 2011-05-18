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

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

public interface SubResource extends Resource {
	
	Object create(String parentUniqueId, SimpleObject post, RequestContext context) throws ResponseException;
	
	Object retrieve(String parentUniqueId, String uuid, RequestContext context) throws ResponseException;
	
	Object update(String parentUniqueId, String uuid, SimpleObject propertiesToUpdate, RequestContext context)
	        throws ResponseException;
	
	void delete(String parentUniqueId, String uuid, String reason, RequestContext context) throws ResponseException;
	
	void purge(String parentUniqueId, String uuid, RequestContext context) throws ResponseException;
	
	List<Object> getAll(String parentUniqueId, RequestContext context) throws ResponseException;
	
}
