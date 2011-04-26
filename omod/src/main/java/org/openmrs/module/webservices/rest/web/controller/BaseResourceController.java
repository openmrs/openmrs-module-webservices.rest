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

import org.openmrs.module.webservices.rest.WSUtil;
import org.openmrs.module.webservices.rest.web.StringToOpenmrsObjectConverterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 *
 */
@Controller
public class BaseResourceController {
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		//binder.registerCustomEditor(OpenmrsObject.class, new OpenmrsObjectByUuidEditor());
		GenericConversionService cs = new GenericConversionService();
		cs.addConverterFactory(new StringToOpenmrsObjectConverterFactory());
		binder.setConversionService(cs);
	}
	
	@Autowired
	protected WSUtil wsUtil;

}
