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
package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.LocationResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * Controller for REST web service access to the Location. Supports CRUD on the resource itself.
 */
@Controller
@RequestMapping(value = "/rest/location")
public class LocationController extends BaseCrudController<LocationResource> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.controller.BaseCrudController#getResource()
	 */
	@Override
	public LocationResource getResource() {
		return Context.getService(RestService.class).getResource(LocationResource.class);
	}
	
	/**
	 * Processes requests to fetch a location by the specified name
	 * 
	 * @param name
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "name")
	@ResponseBody
	public Object findByUniqueName(@RequestParam("name") String name, WebRequest request) throws ResponseException {
		return getResource().findByUniqueName(name, RestUtil.getRequestContext(request));
	}
}
