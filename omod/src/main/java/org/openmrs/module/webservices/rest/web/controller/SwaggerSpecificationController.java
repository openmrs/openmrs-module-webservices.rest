/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.docs.swagger.SwaggerSpecificationCreator;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("SwaggerSpecificationController")
@RequestMapping("/module/webservices/rest/swagger.json")
public class SwaggerSpecificationController {
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	String getSwaggerSpecification(HttpServletRequest request) throws Exception {
		
		String swaggerSpecificationJSON = "";
		StringBuilder baseUrl = new StringBuilder();
		String scheme = request.getScheme();
		int port = request.getServerPort();
		
		baseUrl.append(scheme); // http, https
		baseUrl.append("://");
		baseUrl.append(request.getServerName());
		if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
			baseUrl.append(':');
			baseUrl.append(request.getServerPort());
		}
		
		baseUrl.append(request.getContextPath());
		
		String resourcesUrl = Context.getAdministrationService().getGlobalProperty(
		    RestConstants.URI_PREFIX_GLOBAL_PROPERTY_NAME, baseUrl.toString());
		
		if (!resourcesUrl.endsWith("/")) {
			resourcesUrl += "/";
		}
		
		resourcesUrl += "ws/rest";
		
		String urlWithoutScheme = "";
		
		/* Swagger appends scheme to urls, so we should remove it */
		if (scheme.equals("http"))
			urlWithoutScheme = resourcesUrl.replace("http://", "");
		
		else if (scheme.equals("https"))
			urlWithoutScheme = resourcesUrl.replace("https://", "");
		
		SwaggerSpecificationCreator creator = new SwaggerSpecificationCreator(urlWithoutScheme);
		
		swaggerSpecificationJSON = creator.BuildJSON();
		
		return swaggerSpecificationJSON;
		
	}
	
}
