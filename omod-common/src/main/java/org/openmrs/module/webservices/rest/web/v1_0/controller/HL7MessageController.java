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
import org.openmrs.api.context.Context;
import org.openmrs.hl7.HL7Source;
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

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.Terser;

/**
 * Controller for REST web service access to the HL7 message resource. Supports pushing, retrieving
 * and reviewing on the resource itself.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/hl7")
public class HL7MessageController extends BaseCrudController<HL7MessageResource> {
	
	/**
	 * Overridden to disable handling POST requests by this method as they need to be handled in a
	 * special way by {@link #create(String, HttpServletRequest, HttpServletResponse)}. No two
	 * methods can handle the same RequestMethod.
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
			//hl7 is wrapped up in a json format
			SimpleObject object = SimpleObject.parseJson(hl7);
			hl7 = (String) object.get("hl7");
		}
		
		try {
			Parser parser = new GenericParser();
			Message msg = parser.parse(hl7);
			Terser terser = new Terser(msg);
			
			String source = terser.get("MSH-4");
			String sourceKey = terser.get("MSH-10");
			
			post.add("source", source);
			post.add("sourceKey", sourceKey);
			post.add("data", hl7);
			
			HL7Source hl7Source = Context.getHL7Service().getHL7SourceByName(source);
			if (hl7Source == null) {
				throw new ConversionException("The " + source + " source was not recognized");
			}
		}
		catch (HL7Exception e) {
			throw new ConversionException(e.getMessage(), e);
		}
		
		Object created = getResource().create(post, context);
		return RestUtil.created(response, created);
	}
}
