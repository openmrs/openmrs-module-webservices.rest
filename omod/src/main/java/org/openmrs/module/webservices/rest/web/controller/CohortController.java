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

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.CohortResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * Controller for REST web service access to the Cohort resource. Supports CRUD
 * on the resource itself, and listing and addition of some subresources.
 */
@Controller
@RequestMapping(value = "/rest/cohort")
public class CohortController extends BaseCrudController<CohortResource> {
	
	@Override
	public CohortResource getResource() {
		return Context.getService(RestService.class).getResource(CohortResource.class);
	}
	
	/**
	 * @param query
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 * @should return null if there are no matching cohort
	 * @should find matching cohort
	 */
	@RequestMapping(method = RequestMethod.GET, params = "q")
	@ResponseBody
	public Object findCohort(@RequestParam("q") String query, WebRequest request, HttpServletResponse response)
	        throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		context.setRepresentation(Representation.DEFAULT);
		CohortResource resource = getResource();
		return resource.getCohortByName(query);
	}
	
	/**
	 * @param uuid
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{parentUuid}/patients", method = RequestMethod.GET)
	@ResponseBody
	public List<Object> getCohortMemebers(@PathVariable("parentUuid") String parentUuid, WebRequest request)
	        throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		context.setRepresentation(Representation.REF);
		CohortResource resource = getResource();
		return resource.getCohortMembers(parentUuid);
	}
	
}
