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

import java.util.Set;

import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Beans implementing this interface will be passed search requests.
 */
public interface SearchHandler {
	
	/**
	 * The id is used to resolve ambiguous search requests by specifying ?s=$id
	 * 
	 * @return the unique id
	 */
	String getId();
	
	/**
	 * @return the name of supported resource
	 */
	String getSupportedResource();
	
	/**
	 * @return the list of supported OpenMRS version e.g. "1.8.*","1.9.*"
	 */
	Set<String> getSupportedOpenmrsVersions();
	
	/**
	 * @return the set of search queries
	 */
	Set<SearchQuery> getSearchQueries();
	
	/**
	 * The search.
	 * 
	 * @param context
	 * @return the result
	 * @throws ResponseException
	 */
	PageableResult search(RequestContext context) throws ResponseException;
}
