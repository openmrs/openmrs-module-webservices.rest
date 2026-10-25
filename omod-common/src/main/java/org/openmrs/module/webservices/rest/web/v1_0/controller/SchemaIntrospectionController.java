/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.api.SchemaIntrospectionService;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller that provides schema introspection for REST resources.
 * This allows clients to discover what properties are available for a given resource.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/schemaintrospector")
public class SchemaIntrospectionController extends BaseRestController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private RestService restService;
	
	@Autowired
	private SchemaIntrospectionService schemaIntrospectionService;
	
	/**
	 * Gets all properties available on a resource's delegate domain object
	 * 
	 * @param resourceName The name of the resource to introspect
	 * @return A JSON object containing the resource properties
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{resourceName}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getResourceProperties(@PathVariable("resourceName") String resourceName) throws ResponseException {
		// Get the resource by name with proper prefix
		Resource resource;
		try {
			// Try with the full path (which is how resources are registered)
			String fullResourceName = RestConstants.VERSION_1 + "/" + resourceName;
			log.debug("Looking for resource with name: " + fullResourceName);
			resource = restService.getResourceByName(fullResourceName);
		}
		catch (Exception e) {
			log.error("Failed to find resource with name: " + resourceName, e);
			throw new ObjectNotFoundException("Resource '" + resourceName + "' was not found");
		}
		
		if (resource == null) {
			log.error("Resource is null for name: " + resourceName);
			throw new ObjectNotFoundException("Resource '" + resourceName + "' was not found");
		}
		
		// Get the delegate type for the resource
		Class<?> delegateType = schemaIntrospectionService.getDelegateType(resource);
		if (delegateType == null) {
			log.error("Could not determine delegate type for resource: " + resourceName);
			throw new ObjectNotFoundException("Could not determine delegate type for resource '" + resourceName + "'");
		}
		
		// Get properties for the delegate type
		Map<String, String> properties = schemaIntrospectionService.discoverAvailableProperties(delegateType);
		
		// Build the response
		SimpleObject result = new SimpleObject();
		result.add("resourceName", resourceName);
		result.add("delegateType", delegateType.getName());
		result.add("discoverableProperties", properties);
		
		return result;
	}
}