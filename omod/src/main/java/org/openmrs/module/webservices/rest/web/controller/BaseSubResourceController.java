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

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.resource.api.SubResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * Base controller that handles exceptions (via {@link BaseRestController}) and also standard CRUD
 * operations based on a {@link SubResource}.
 * 
 * @param <R>
 */
public abstract class BaseSubResourceController<R extends SubResource> extends BaseRestController {
	
	public abstract R getResource();
	
	/**
	 * @param parentUuid
	 * @param uuid
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public Object retrieve(@PathVariable("parentUuid") String parentUuid, @PathVariable("uuid") String uuid,
	                       WebRequest request) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		R resource = getResource();
		return resource.retrieve(parentUuid, uuid, context);
	}
	
	/**
	 * @param parentUuid
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Object> getAll(@PathVariable("parentUuid") String parentUuid, WebRequest request,
	                           HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		//context.setRepresentation(Representation.DEFAULT);
		return getResource().getAll(parentUuid, context);
	}
	
	/**
	 * @param parentUuid
	 * @param post
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object create(@PathVariable("parentUuid") String parentUuid, @RequestBody SimpleObject post, WebRequest request,
	                     HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request);
		Object created = getResource().create(parentUuid, post, context);
		return RestUtil.created(response, created);
	}
	
	// TODO update
	
	// TODO delete
	
	// TODO purge
	
}
