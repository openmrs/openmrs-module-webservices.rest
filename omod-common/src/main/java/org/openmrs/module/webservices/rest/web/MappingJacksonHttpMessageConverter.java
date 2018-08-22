/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web;

import org.openmrs.api.context.Context;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

@Configuration
public class MappingJacksonHttpMessageConverter {
	
	private static final String PACKAGE = "org.springframework.http.converter.json.";
	
	private static final String OLD_CLASS_NAME = PACKAGE + "MappingJacksonHttpMessageConverter";
	
	private static final String NEW_CLASS_NAME = PACKAGE + "MappingJackson2HttpMessageConverter";
	
	@Bean(name = "jsonHttpMessageConverter")
	public HttpMessageConverter getMappingJacksonHttpMessageConverter() throws Exception {
		
		Class<?> clazz;
		try {
			clazz = Context.loadClass(NEW_CLASS_NAME);
		}
		catch (ClassNotFoundException e) {
			clazz = Context.loadClass(OLD_CLASS_NAME);
		}
		
		return (HttpMessageConverter) clazz.newInstance();
	}
	
}
