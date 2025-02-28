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

import static org.hamcrest.Matchers.is;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.UnknownResourceException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.mock.web.MockHttpServletRequest;

public class NameTemplateController2_0Test extends RestControllerTestUtils {
	
	private static final String URL_GLOBAL_NAMETEMPLATE = "global";
	
	private static final String DEFAULT_NAMETEMPLATE = "short";
	private static final String DEFAULT_NAMETEMPLATE_RESOURCE = "nameTemplateShort.json";
	private static final String ALTERNATE_NAMETEMPLATE = "long";
	private static final String ALTERNATE_NAMETEMPLATE_RESOURCE = "nameTemplateLong.json";
	private static final String UNKNOWN_NAMETEMPLATE = "foo";
	
	private String originalNametemplate = null;
	
	public String getURI() {
		return "nametemplate";
	}
	
	@Before
	public void before() throws Exception {
		AdministrationService service = Context.getAdministrationService();
		originalNametemplate = service.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_LAYOUT_NAME_FORMAT);
		service.setGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_LAYOUT_NAME_FORMAT, DEFAULT_NAMETEMPLATE);
	}
	
	@After
	public void after() throws Exception {
		if (!StringUtils.isEmpty(originalNametemplate)) {
			AdministrationService service = Context.getAdministrationService();
			service.setGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_LAYOUT_NAME_FORMAT, originalNametemplate);
		}
	}
	
	@Test
	public void shouldGetNameTemplate() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI());
		SimpleObject result = deserialize(handle(req));
		
		String json;
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_NAMETEMPLATE_RESOURCE)) {
			json = IOUtils.toString(inputStream, "UTF-8");
		}
		
		Assert.assertThat(result, is(SimpleObject.parseJson(json)));
	}
	
	@Test
	public void shouldGetGlobalNameTemplate() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI() + "/" + URL_GLOBAL_NAMETEMPLATE);
		SimpleObject result = deserialize(handle(req));
		
		String json;
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_NAMETEMPLATE_RESOURCE)) {
			json = IOUtils.toString(inputStream, "UTF-8");
		}
		
		Assert.assertThat(result, is(SimpleObject.parseJson(json)));
	}
	
	@Test
	public void shouldGetNameTemplateByCodename() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI() + "/" + ALTERNATE_NAMETEMPLATE);
		SimpleObject result = deserialize(handle(req));
		
		String json;
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(ALTERNATE_NAMETEMPLATE_RESOURCE)) {
			json = IOUtils.toString(inputStream, "UTF-8");
		}
		
		Assert.assertThat(result, is(SimpleObject.parseJson(json)));
	}
	
	@Test
	public void shouldReturnNotFoundForUnknownCodename() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI() + "/" + UNKNOWN_NAMETEMPLATE);
		UnknownResourceException handledException = null;
		try {
			handle(req);
		}
		catch (UnknownResourceException ex) {
			handledException = ex;
		}
		Assert.assertNotNull(handledException);
	}
}
