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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.api.SubResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Base controller that handles exceptions (via {@link BaseRestController}) and also standard CRUD
 * operations based on a {@link SubResource}.
 * 
 * @param <R>
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1)
public class MainSubResourceController extends BaseRestController {
	
	@Autowired
	RestService restService;
	
	/**
	 * @param parentUuid
	 * @param uuid
	 * @param request
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{resource}/{parentUuid}/{subResource}/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public Object retrieve(@PathVariable("resource") String resource, @PathVariable("parentUuid") String parentUuid,
	        @PathVariable("subResource") String subResource, @PathVariable("uuid") String uuid, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request, response);
		SubResource res = (SubResource) restService.getResourceByName(buildResourceName(resource) + "/" + subResource);
		return res.retrieve(parentUuid, uuid, context);
	}
	
	/**
	 * @param parentUuid
	 * @param request
	 * @param response
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/{resource}/{parentUuid}/{subResource}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getAll(@PathVariable("resource") String resource, @PathVariable("parentUuid") String parentUuid,
	        @PathVariable("subResource") String subResource, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request, response);
		SubResource res = (SubResource) restService.getResourceByName(buildResourceName(resource) + "/" + subResource);
		return res.getAll(parentUuid, context);
	}
	
	/**
	 * @param parentUuid
	 * @param post
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{resource}/{parentUuid}/{subResource}", method = RequestMethod.POST)
	@ResponseBody
	public Object create(@PathVariable("resource") String resource, @PathVariable("parentUuid") String parentUuid,
	        @PathVariable("subResource") String subResource, @RequestBody SimpleObject post, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request, response);
		SubResource res = (SubResource) restService.getResourceByName(buildResourceName(resource) + "/" + subResource);
		Object created = res.create(parentUuid, post, context);
		return RestUtil.created(response, created);
	}
	
	/**
	 * @param parentUuid
	 * @param uuid
	 * @param post
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{resource}/{parentUuid}/{subResource}/{uuid}", method = RequestMethod.POST)
	@ResponseBody
	public Object update(@PathVariable("resource") String resource, @PathVariable("parentUuid") String parentUuid,
	        @PathVariable("subResource") String subResource, @PathVariable("uuid") String uuid,
	        @RequestBody SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request, response);
		SubResource res = (SubResource) restService.getResourceByName(buildResourceName(resource) + "/" + subResource);
		Object updated = res.update(parentUuid, uuid, post, context);
		return RestUtil.updated(response, updated);
	}
	
	/**
	 * @param parentUuid
	 * @param uuid
	 * @param reason
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "/{resource}/{parentUuid}/{subResource}/{uuid}", method = RequestMethod.DELETE, params = "!purge")
	@ResponseBody
	public Object delete(@PathVariable("resource") String resource, @PathVariable("parentUuid") String parentUuid,
	        @PathVariable("subResource") String subResource, @PathVariable("uuid") String uuid,
	        @RequestParam(value = "reason", defaultValue = "web service call") String reason, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request, response);
		SubResource res = (SubResource) restService.getResourceByName(buildResourceName(resource) + "/" + subResource);
		res.delete(parentUuid, uuid, reason, context);
		return RestUtil.noContent(response);
	}
	
	/**
	 * @param parentUuid
	 * @param uuid
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/{resource}/{parentUuid}/{subResource}/{uuid}", method = RequestMethod.DELETE, params = "purge")
	@ResponseBody
	public Object purge(@PathVariable("resource") String resource, @PathVariable("parentUuid") String parentUuid,
	        @PathVariable("subResource") String subResource, @PathVariable("uuid") String uuid, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		RequestContext context = RestUtil.getRequestContext(request, response);
		SubResource res = (SubResource) restService.getResourceByName(buildResourceName(resource) + "/" + subResource);
		res.purge(parentUuid, uuid, context);
		return RestUtil.noContent(response);
	}
	
}
