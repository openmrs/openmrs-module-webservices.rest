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
import org.openmrs.module.webservices.rest.web.resource.ConceptDatatypeResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * Controller for REST web service access to the ConceptDatatype resource. Supports CRUD on the
 * resource itself.
 */
@Controller
@RequestMapping(value = "/rest/conceptdatatype")
public class ConceptDatatypeController extends BaseRestController {
	
	private ConceptDatatypeResource getConceptDatatypeResource() {
		return Context.getService(RestService.class).getResource(ConceptDatatypeResource.class);
	}
	
	/**
	 * Processes requests to retrieve a concept datatype with a specified uuid from the database
	 * 
	 * @param uuid
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public Object getConceptDatatype(@PathVariable("uuid") String uuid, WebRequest request) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		ConceptDatatypeResource resource = getConceptDatatypeResource();
		return resource.retrieve(uuid, context);
	}
	
	/**
	 * Processes requests to fetch all concept datatypes currently in the database including retired
	 * ones
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Object> getConceptDatatypes(WebRequest request, HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		context.setRepresentation(Representation.REF);
		ConceptDatatypeResource resource = getConceptDatatypeResource();
		return resource.getAll(context);
	}
	
	/**
	 * Processes requests to insert new concept datatypes into the database
	 * 
	 * @param post
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object createConceptDatatype(@RequestBody SimpleObject post, WebRequest request, HttpServletResponse response)
	                                                                                                                     throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		Object created = getConceptDatatypeResource().create(post, context);
		return RestUtil.created(response, created);
	}
	
	/**
	 * Processes requests to update existing concept datatypes
	 * 
	 * @param uuid
	 * @param post
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.POST)
	@ResponseBody
	public Object updateConceptDatatype(@PathVariable("uuid") String uuid, @RequestBody SimpleObject post,
	                                    WebRequest request, HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		ConceptDatatypeResource resource = getConceptDatatypeResource();
		resource.update(uuid, post, context);
		return RestUtil.noContent(response);
	}
	
	/**
	 * Processes requests to deleted a concept datatype from the database
	 * 
	 * @param uuid
	 * @param reason
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE, params = "!purge")
	@ResponseBody
	public Object retireConceptDatatype(@PathVariable("uuid") String uuid,
	                                    @RequestParam(value = "reason", defaultValue = "web service call") String reason,
	                                    WebRequest request, HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		getConceptDatatypeResource().delete(uuid, reason, context);
		return RestUtil.noContent(response);
	}
	
	/**
	 * Processes requests to purge a concept datatype from the database
	 * 
	 * @param uuid
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE, params = "purge=true")
	@ResponseBody
	public Object purgeConceptDatatype(@PathVariable("uuid") String uuid, WebRequest request, HttpServletResponse response)
	                                                                                                                       throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		getConceptDatatypeResource().purge(uuid, context);
		return RestUtil.noContent(response);
	}
	
}
