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
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.PatientResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * Controller for REST web service access to the Patient resource. Supports CRUD on the resource
 * itself, and listing and addition of some subresources.
 */
@Controller
@RequestMapping(value = "/rest/patient")
public class PatientController extends BaseCrudController<PatientResource> {
	
	@Override
	public PatientResource getResource() {
		return Context.getService(RestService.class).getResource(PatientResource.class);
	}
	
	/**
	 * @param query
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 * @should return no results if there are no matching patients
	 * @should find matching patients
	 */
	@RequestMapping(method=RequestMethod.GET, params="q")
	@ResponseBody
	public List<Object> findPatients(@RequestParam("q") String query, WebRequest request, HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		context.setRepresentation(Representation.REF);
		PatientResource resource = getResource();
		return resource.search(query, context);
	}
	
}
