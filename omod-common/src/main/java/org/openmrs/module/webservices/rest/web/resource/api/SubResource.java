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
 * A resource that belongs completely to another resource. You only interact with this resource
 * through its parent resource (e.g. adding children to a parent, listing all children of a parent)
 * but not directly (e.g. you cannot search for all sub-resources of any parent with a given
 * characteristic.
 */
public interface SubResource extends Resource {
	
	/**
	 * Add a sub-resource to a parent resource
	 * 
	 * @param parentUniqueId desc needed
	 * @param post desc needed
	 * @param context desc needed
	 * @return desc needed
	 * @throws ResponseException desc needed
	 */
	Object create(String parentUniqueId, SimpleObject post, RequestContext context) throws ResponseException;
	
	/**
	 * Fetch the given sub-resource of the given parent resource
	 * 
	 * @param parentUniqueId desc needed
	 * @param uniqueId desc needed
	 * @param context desc needed
	 * @return desc needed
	 * @throws ResponseException desc needed
	 */
	Object retrieve(String parentUniqueId, String uniqueId, RequestContext context) throws ResponseException;
	
	/**
	 * Edit an existing sub-resource of a given parent resource
	 * 
	 * @param parentUniqueId desc needed
	 * @param uniqueId desc needed
	 * @param propertiesToUpdate desc needed
	 * @param context desc needed
	 * @return desc needed
	 * @throws ResponseException desc needed
	 */
	Object update(String parentUniqueId, String uniqueId, SimpleObject propertiesToUpdate, RequestContext context)
	        throws ResponseException;
	
	/**
	 * Remove an existing sub-resource from a parent resource (voiding it or retiring it)
	 * 
	 * @param parentUniqueId desc needed
	 * @param uniqueId desc needed
	 * @param reason desc needed
	 * @param context desc needed
	 * @throws ResponseException desc needed
	 */
	void delete(String parentUniqueId, String uniqueId, String reason, RequestContext context) throws ResponseException;
	
	/**
	 * Completely removes an existing sub-resource from persistent storage
	 * 
	 * @param parentUniqueId desc needed
	 * @param uniqueId desc needed
	 * @param context desc needed
	 * @throws ResponseException desc needed
	 */
	void purge(String parentUniqueId, String uniqueId, RequestContext context) throws ResponseException;
	
	/**
	 * Lists all instances of this sub-resource that belong to the given parent resource
	 * 
	 * @param parentUniqueId desc needed
	 * @param context desc needed
	 * @return desc needed
	 * @throws ResponseException desc needed
	 */
	SimpleObject getAll(String parentUniqueId, RequestContext context) throws ResponseException;
	
}
