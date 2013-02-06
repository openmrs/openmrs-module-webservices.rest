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
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.OrderResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the {@link OrderResource}.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/order")
public class OrderController {
	
	@Autowired
	RestService restService;
	
	/**
	 * Fetch orders for a given patient
	 * 
	 * @param patientUuid
	 * @param request
	 * @param response
	 * @return all orders for the given patient
	 * @throws ResponseException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "patient")
	@WSDoc("Fetch all non-voided orders for the patient with the given uuid. Use type=(subtype) to limit to a particular subtype.")
	@ResponseBody
	public SimpleObject searchByPatient(@RequestParam("patient") String patientUuid, HttpServletRequest request,
	        HttpServletResponse response) throws ResponseException {
		OrderResource resource = (OrderResource) restService.getResourceByName("order");
		return resource.getOrdersByPatient(patientUuid, RestUtil.getRequestContext(request)).toSimpleObject();
	}
	
}
