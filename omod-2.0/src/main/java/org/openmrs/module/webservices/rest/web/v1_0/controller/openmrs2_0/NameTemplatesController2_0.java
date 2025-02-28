/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_0;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.layout.name.NameSupport;
import org.openmrs.layout.name.NameTemplate;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.module.webservices.rest.web.v1_0.helper.LayoutTemplateProvider;
import org.openmrs.serialization.SerializationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * API endpoint to get a list of all available NameTemplates.
 * <p>
 * Unfortunately, there already exists a "/nametemplate" endpoint - see NameTemplateController2_0
 * so to avoid breaking the existing API, we use '/nametemplates' as the endpoint for the list.
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/nametemplates")
public class NameTemplatesController2_0 extends BaseRestController {
	
	@RequestMapping(method = RequestMethod.GET)
	@WSDoc("Get the list of all known name layout templates.")
	@ResponseBody
	public Object get(WebRequest request) throws SerializationException {
		LayoutTemplateProvider<NameTemplate> provider = getTemplateProvider();
		List<NameTemplate> nameTemplates = provider.getAllLayoutTemplates();
		List<SimpleObject> nameTemplatesSO = new ArrayList<>(nameTemplates.size());
		for (NameTemplate template : nameTemplates) {
			nameTemplatesSO.add(provider.asRepresentation(template));
		}
		SimpleObject result = new SimpleObject();
		result.add("results", nameTemplatesSO);
		return result;
	}
	
	private static LayoutTemplateProvider<NameTemplate> getTemplateProvider() {
		return new LayoutTemplateProvider<>(NameSupport.getInstance(), NameTemplateController2_0.LAYOUT_NAME_DEFAULTS);
	}
}
