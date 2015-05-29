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

import java.util.List;
import java.util.Map;

import org.openmrs.api.APIException;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler;

/**
 * Database related methods for the Rest Web Services
 */
public interface RestService {
	
	/**
	 * Parses a representation requested by the client via the http request
	 * 
	 * @param requested
	 * @return
	 * @should get ref representation when specified
	 * @should get default representation when specified
	 * @should get full representation when specified
	 * @should get a named representation when specified
	 */
	public Representation getRepresentation(String requested);
	
	Resource getResourceByName(String name) throws APIException;
	
	/**
	 * Auto generated method comment
	 * 
	 * @param supportedClass
	 * @return
	 * @throws APIException
	 */
	Resource getResourceBySupportedClass(Class<?> supportedClass) throws APIException;
	
	/**
	 * Returns a search handler, which supports the given resource and the map of parameters and
	 * values.
	 * 
	 * @param resourceName
	 * @param parameters
	 * @return searchHandler or <code>null</code> if no match
	 * @throws APIException
	 */
	SearchHandler getSearchHandler(String resourceName, Map<String, String[]> parameters) throws APIException;
	
	/**
	 * Returns all {@link DelegatingResourceHandler}s
	 * 
	 * @return list of {@link DelegatingResourceHandler}s
	 * @throws APIException
	 */
	public List<DelegatingResourceHandler<?>> getResourceHandlers() throws APIException;
	
	/**
	 * Initializes all Resources and Search handlers for use; called after module startup
	 */
	public void initialize();
	
	public List<SearchHandler> getAllSearchHandlers();
}
