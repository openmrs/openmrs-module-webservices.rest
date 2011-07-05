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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.resource.ResourceData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller behind the "help.jsp" page. Should list off available urls and representations.
 */
@Controller
@RequestMapping("/module/webservices/rest/help")
public class HelpController {
	
	@RequestMapping(method = RequestMethod.GET)
	public void showPage(ModelMap map, HttpServletRequest request) throws IOException {
		
		// TODO put content into map about controller annotations and resource
		// views
		
		List<ResourceData> resources = new ArrayList<ResourceData>();
		
		StringBuffer url = new StringBuffer();
		String scheme = request.getScheme();
		int port = request.getServerPort();
		
		url.append(scheme); // http, https
		url.append("://");
		url.append(request.getServerName());
		if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
			url.append(':');
			url.append(request.getServerPort());
		}
		
		url.append(request.getContextPath());
		url.append("/ws");
		
		String rootPath = url.toString();
		
		List<Class<?>> controllers = RestUtil.getClassesForPackage("org.openmrs.module.webservices.rest.web.controller",
		    "Controller.class");
		
		for (Class<?> cls : controllers) {
			RequestMapping annotation = (RequestMapping) cls.getAnnotation(RequestMapping.class);
			if (annotation == null)
				continue;
			
			if (cls.getSimpleName().equals("BaseRestController") || cls.getSimpleName().equals("SettingsFormController")
			        || cls.getSimpleName().equals("SessionController") || cls.getSimpleName().equals("HelpController")) {
				continue;
			}
			
			resources.add(new ResourceData(cls.getSimpleName().replace("Controller", ""), rootPath + annotation.value()[0]));
		}
		
		map.put("data", resources);
	}
}
