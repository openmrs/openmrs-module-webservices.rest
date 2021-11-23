/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
	 * @param requested representation
	 * @return
	 * <strong>Should</strong> get ref representation when specified
	 * <strong>Should</strong> get default representation when specified
	 * <strong>Should</strong> get full representation when specified
	 * <strong>Should</strong> get a named representation when specified
	 */
	Representation getRepresentation(String requested);
	
	/**
	 * Gets resource by name.
	 *
	 * @param name the name
	 * @return the resource by name
	 * @throws APIException the api exception
	 */
	Resource getResourceByName(String name) throws APIException;
	
	/**
	 * Gets resource by name or uuid.
	 * 
	 * @param str the name or uuid
	 * @return the resource by name or uuid
	 * @throws APIException the api exception
	 */
	Resource getResourceByNameOrUuid(String str) throws APIException;
	
	/**
	 * Gets resource by supported class
	 * 
	 * @param supportedClass the supported class
	 * @return resource by supported class
	 * @throws APIException the api exception
	 */
	Resource getResourceBySupportedClass(Class<?> supportedClass) throws APIException;
	
	/**
	 * Returns a search handler, which supports the given resource and the map of parameters and
	 * values.
     * @param resourceName the resource name
     * @param parameters   the parameters
	 * @return searchHandler or <code>null</code> if no match
	 * @throws APIException the api exception
	 */
	SearchHandler getSearchHandler(String resourceName, Map<String, String[]> parameters) throws APIException;
	
	/**
	 * Returns all search handlers supporting a resource
	 * 
	 * @param resourceName the resource name
	 * @return search handlers
	 */
	Set<SearchHandler> getSearchHandlers(String resourceName);
	
	/**
	 * Returns all {@link DelegatingResourceHandler}s
	 * 
	 * @return list of {@link DelegatingResourceHandler}s
	 * @throws APIException the api exception
	 */
	List<DelegatingResourceHandler<?>> getResourceHandlers() throws APIException;
	
	/**
	 * Initializes all Resources and Search handlers for use; called after module startup
	 */
	void initialize();
	
	/**
	 * Returns all search handlers.
	 * 
	 * @return all search handlers or <code>null</code> if none registered
	 */
	List<SearchHandler> getAllSearchHandlers();
}
