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
package org.openmrs.module.webservices.rest.web.v1_0.controller;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.api.Creatable;
import org.openmrs.module.webservices.rest.web.resource.api.CrudResource;
import org.openmrs.module.webservices.rest.web.resource.api.Deletable;
import org.openmrs.module.webservices.rest.web.resource.api.Listable;
import org.openmrs.module.webservices.rest.web.resource.api.Purgeable;
import org.openmrs.module.webservices.rest.web.resource.api.Retrievable;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.resource.api.Updatable;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * Base controller that handles exceptions (via {@link BaseRestController}) and also standard CRUD
 * operations based on a {@link CrudResource}.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1)
public class MainResourceController extends BaseRestController {
	
	@Autowired
	RestService restService;
	
	/**
	 * @param uuid
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{resource}/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public Object retrieve(@PathVariable("resource") String resource, @PathVariable("uuid") String uuid,
	        HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request, response);
		Retrievable res = (Retrievable) restService.getResourceByName(buildResourceName(resource));
		return res.retrieve(uuid, context);
	}
	
	/**
	 * @param post
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{resource}", method = RequestMethod.POST)
	@ResponseBody
	public Object create(@PathVariable("resource") String resource, @RequestBody SimpleObject post,
	        HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request, response);
		Creatable res = (Creatable) restService.getResourceByName(buildResourceName(resource));
		Object created = res.create(post, context);
		return RestUtil.created(response, created);
	}
	
	/**
	 * @param uuid
	 * @param post
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{resource}/{uuid}", method = RequestMethod.POST)
	@ResponseBody
	public Object update(@PathVariable("resource") String resource, @PathVariable("uuid") String uuid,
	        @RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request, response);
		Updatable res = (Updatable) restService.getResourceByName(buildResourceName(resource));
		Object updated = res.update(uuid, post, context);
		return RestUtil.updated(response, updated);
	}
	
	/**
	 * @param uuid
	 * @param reason
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "/{resource}/{uuid}", method = RequestMethod.DELETE, params = "!purge")
	@ResponseBody
	public Object delete(@PathVariable("resource") String resource, @PathVariable("uuid") String uuid,
	        @RequestParam(value = "reason", defaultValue = "web service call") String reason, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request, response);
		Deletable res = (Deletable) restService.getResourceByName(buildResourceName(resource));
		res.delete(uuid, reason, context);
		return RestUtil.noContent(response);
	}
	
	/**
	 * @param uuid
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/{resource}/{uuid}", method = RequestMethod.DELETE, params = "purge")
	@ResponseBody
	public Object purge(@PathVariable("resource") String resource, @PathVariable("uuid") String uuid,
	        HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request, response);
		Purgeable res = (Purgeable) restService.getResourceByName(buildResourceName(resource));
		res.purge(uuid, context);
		return RestUtil.noContent(response);
	}
	
	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{resource}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject get(@PathVariable("resource") String resource, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		Object res = restService.getResourceByName(buildResourceName(resource));
		Converter conv = res instanceof Converter ? (Converter) res : null;
		
		RequestContext context = RestUtil.getRequestContext(request, response, Representation.REF);
		
		@SuppressWarnings("unchecked")
		SearchHandler searchHandler = restService.getSearchHandler(buildResourceName(resource), request.getParameterMap());
		if (searchHandler != null) {
			return searchHandler.search(context).toSimpleObject(conv);
		}
		
		Enumeration parameters = request.getParameterNames();
		while (parameters.hasMoreElements()) {
			if (!RestConstants.SPECIAL_REQUEST_PARAMETERS.contains(parameters.nextElement())) {
				if (res instanceof Searchable) {
					return ((Searchable) res).search(context);
				} else {
					throw new ResourceDoesNotSupportOperationException(res.getClass().getSimpleName() + " is not searchable");
				}
			}
		}
		
		if (res instanceof Listable) {
			return ((Listable) res).getAll(context);
		} else {
			throw new ResourceDoesNotSupportOperationException(res.getClass().getSimpleName() + " is not listable");
		}
	}
	
}
