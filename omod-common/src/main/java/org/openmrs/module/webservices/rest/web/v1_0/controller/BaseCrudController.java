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

import java.lang.reflect.ParameterizedType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.CrudResource;
import org.openmrs.module.webservices.rest.web.resource.api.Listable;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Base controller that handles exceptions (via {@link BaseRestController}) and also standard CRUD
 * operations based on a {@link CrudResource}.
 * 
 * @param <R>
 */
public abstract class BaseCrudController<R extends CrudResource> extends BaseRestController {
	
	/**
	 * @return a Resource for the actual parameterized type of this superclass
	 */
	@SuppressWarnings("unchecked")
	protected R getResource() {
		ParameterizedType t = (ParameterizedType) getClass().getGenericSuperclass();
		Class<R> clazz = (Class<R>) t.getActualTypeArguments()[0];
		return Context.getService(RestService.class).getResource(clazz);
	}
	
	/**
	 * @param uuid
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public Object retrieve(@PathVariable("uuid") String uuid, HttpServletRequest request) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		R resource = getResource();
		return resource.retrieve(uuid, context);
	}
	
	/**
	 * @param post
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object create(@RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		Object created = getResource().create(post, context);
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
	@RequestMapping(value = "/{uuid}", method = RequestMethod.POST)
	@ResponseBody
	public Object update(@PathVariable("uuid") String uuid, @RequestBody SimpleObject post, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		CrudResource resource = getResource();
		resource.update(uuid, post, context);
		return RestUtil.noContent(response);
	}
	
	/**
	 * @param uuid
	 * @param reason
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE, params = "!purge")
	@ResponseBody
	public Object delete(@PathVariable("uuid") String uuid,
	        @RequestParam(value = "reason", defaultValue = "web service call") String reason, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		getResource().delete(uuid, reason, context);
		return RestUtil.noContent(response);
	}
	
	/**
	 * @param uuid
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE, params = "purge")
	@ResponseBody
	public Object purge(@PathVariable("uuid") String uuid, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		getResource().purge(uuid, context);
		return RestUtil.noContent(response);
	}
	
	/**
	 * @param query
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "q")
	@ResponseBody
	public SimpleObject search(@RequestParam("q") String query, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		Searchable searchable;
		try {
			searchable = (Searchable) getResource();
		}
		catch (ClassCastException ex) {
			throw new ResourceDoesNotSupportOperationException(getResource().getClass().getSimpleName()
			        + " is not Searchable", null);
		}
		RequestContext context = RestUtil.getRequestContext(request, Representation.REF);
		return searchable.search(query, context);
	}
	
	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getAll(HttpServletRequest request, HttpServletResponse response) throws ResponseException {
		Listable listable;
		try {
			listable = (Listable) getResource();
		}
		catch (ClassCastException ex) {
			throw new ResourceDoesNotSupportOperationException(
			        getResource().getClass().getSimpleName() + " is not Listable", null);
		}
		RequestContext context = RestUtil.getRequestContext(request, Representation.REF);
		return listable.getAll(context);
	}
	
}
