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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.docs.ResourceDocCreator;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller behind the "help.jsp" page. Should list off available urls and representations.
 */
@Controller("webservices.rest.HelpController")
public class HelpController {
	
	@RequestMapping("/module/webservices/rest/help")
	public void showPage(ModelMap map, HttpServletRequest request) throws IllegalAccessException, InstantiationException,
	        IOException, ConversionException {
		
		// TODO put content into map about controller annotations and resource
		// views
		
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
		
		String url = Context.getAdministrationService().getGlobalProperty(RestConstants.URI_PREFIX_GLOBAL_PROPERTY_NAME,
		    baseUrl.toString());
		
		url += "/ws";
		
		map.put("data", ResourceDocCreator.create(url));
	}
}
