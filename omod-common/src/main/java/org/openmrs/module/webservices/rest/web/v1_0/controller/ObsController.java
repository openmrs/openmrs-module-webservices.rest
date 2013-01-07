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
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.ObsResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the Obs resource. Supports CRUD on the resource itself.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/obs")
public class ObsController {
	
	@Autowired
	RestService restService;
	
	/**
	 * Fetch obs for a given encounter
	 * 
	 * @param encounterUniqueId
	 * @param request
	 * @param response
	 * @return obs for the given encounter
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "encounter")
	@WSDoc("Fetch all non-retired obs for an encounter with the given uuid")
	@ResponseBody
	public SimpleObject searchByEncounter(@RequestParam("encounter") String encounterUniqueId, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		ObsResource resource = (ObsResource) restService.getResourceByName("obs");
		RequestContext context = RestUtil.getRequestContext(request);
		return resource.getObsByEncounter(encounterUniqueId, context);
	}
	
	/**
	 * Fetch obs for a given patient
	 * 
	 * @param patientUuid
	 * @param request
	 * @param response
	 * @return obs for the given patient
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "patient")
	@WSDoc("Fetch all non-voided obs for a patient with the given uuid")
	@ResponseBody
	public SimpleObject searchByPatient(@RequestParam("patient") String patientUuid, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		ObsResource resource = (ObsResource) restService.getResourceByName("obs");
		return resource.getObsByPatient(patientUuid, RestUtil.getRequestContext(request));
	}
	
}
