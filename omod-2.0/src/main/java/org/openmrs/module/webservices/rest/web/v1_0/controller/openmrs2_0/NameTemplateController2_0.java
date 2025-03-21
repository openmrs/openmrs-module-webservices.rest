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

import org.openmrs.layout.name.NameSupport;
import org.openmrs.layout.name.NameTemplate;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.module.webservices.rest.web.v1_0.helper.LayoutTemplateProvider;
import org.openmrs.serialization.SerializationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/nametemplate")
public class NameTemplateController2_0 extends BaseRestController {
	
	public static final String LAYOUT_NAME_DEFAULTS = "layout.name.defaults";
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Object get(WebRequest request) throws SerializationException {
		LayoutTemplateProvider<NameTemplate> provider = getTemplateProvider();
		NameTemplate nameTemplate = provider.getDefaultLayoutTemplate();
		return provider.asRepresentation(nameTemplate);
	}
	
	private static LayoutTemplateProvider<NameTemplate> getTemplateProvider() {
		return new LayoutTemplateProvider<>(NameSupport.getInstance(), LAYOUT_NAME_DEFAULTS);
	}
}
