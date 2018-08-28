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

/**
 * In spring 3.1.2 MappingJacksonHttpMessageConverter was replaced with
 * MappingJackson2HttpMessageConverter and eventually removed in version 4.1.0. This bean
 * configuration class allows the module to run on both pre and post 4.1.0 versions of Spring by
 * loading the new class if it's available in the spring version that OpenMRS is running on
 * otherwise it falls back to the old one.
 */
@Configuration
public class MappingJacksonHttpMessageConverterConfig {
	
	private static final String PACKAGE = "org.springframework.http.converter.json.";
	
	private static final String CLASS_BEFORE_SPRING_4_1 = PACKAGE + "MappingJacksonHttpMessageConverter";
	
	private static final String CLASS_FROM_SPRING_4_1 = PACKAGE + "MappingJackson2HttpMessageConverter";
	
	@Bean(name = "jsonHttpMessageConverter")
	public HttpMessageConverter getMappingJacksonHttpMessageConverter() throws Exception {
		
		Class<?> clazz;
		try {
			clazz = Context.loadClass(CLASS_BEFORE_SPRING_4_1);
		}
		catch (ClassNotFoundException e) {
			clazz = Context.loadClass(CLASS_FROM_SPRING_4_1);
		}
		
		return (HttpMessageConverter) clazz.newInstance();
	}
	
}
