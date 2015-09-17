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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.webservices.docs.ResourceDoc;
import org.openmrs.module.webservices.docs.ResourceDocCreator;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Exposes a catalog of all available resources.
 */
@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/catalog")
public class CatalogController extends BaseRestController {
	
	/**
	 * Gets a catalog of all available resources.
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Object getResourceCatalog(@RequestParam(value = "q", required = false) final String resourceName,
	        HttpServletRequest request) throws Exception {
		List<ResourceDoc> resourceDocList = new ArrayList<ResourceDoc>();
		SimpleObject resourceCatalog = new SimpleObject();
		String prefix = RestConstants.URI_PREFIX;
		//strip the ending string '/rest/' because it will be added by ResourceDocCreator.create
		if (StringUtils.isNotBlank(prefix) && prefix.endsWith("/rest/"))
			prefix = prefix.substring(0, prefix.lastIndexOf("/rest/"));
		if (resourceName == null) {
			resourceDocList = ResourceDocCreator.create(prefix);
		} else {
			for (ResourceDoc resourceDoc : ResourceDocCreator.create(prefix)) {
				if (resourceDoc.getName().toLowerCase().contains(resourceName.toLowerCase())) {
					resourceDocList.add(resourceDoc);
				}
			}
		}
		resourceCatalog.put("catalog", resourceDocList);
		return new LinkedHashMap<String, Object>(resourceCatalog);
	}
}
