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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.ConceptSource;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.HL7MessageResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for REST web service access to the HL7 message resource. Supports pushing, retrieving
 * and reviewing on the resource itself.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/hl7")
public class HL7MessageController extends BaseCrudController<HL7MessageResource> {
	
	/**
	 * Overridden to disable standard POST request processing
	 * 
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudController#create(org.openmrs.module.webservices.rest.SimpleObject,
	 *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@RequestMapping(method = RequestMethod.TRACE)
	@Override
	public Object create(SimpleObject post, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object create(@RequestBody String hl7, HttpServletRequest request, HttpServletResponse response)
	        throws ResponseException, JsonParseException, JsonMappingException, IOException {
		RequestContext context = RestUtil.getRequestContext(request);
		SimpleObject post = new SimpleObject();
		
		if (hl7.trim().startsWith("{")) {
			ObjectMapper objectMapper = new ObjectMapper();
			SimpleObject object = objectMapper.readValue(hl7, SimpleObject.class);
			hl7 = (String) object.get("hl7");
		}
		
		String[] hl7split = hl7.split("\\|");
		if (hl7split.length < 10) {
			throw new ConversionException("The HL7 message is too short or has a wrong format: " + hl7);
		}
		String sourceKey = hl7split[9];
		String source = hl7split[3];
		
		post.add("sourceKey", sourceKey);
		post.add("source", source);
		post.add("data", hl7);
		
		ConceptSource conceptSource = Context.getConceptService().getConceptSourceByName(source);
		if (conceptSource == null) {
			throw new ConversionException("The " + source + " source was not recognized");
		}
		
		Object created = getResource().create(post, context);
		return RestUtil.created(response, created);
	}
}
