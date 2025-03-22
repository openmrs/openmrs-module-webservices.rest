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

import java.io.IOException;
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
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0.RestConstants2_0;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;

public class NameTemplateController2_0Test extends RestControllerTestUtils {
	
	private static final String URL_GLOBAL_NAMETEMPLATE = "layout.name.format";
	
	private static final String DEFAULT_NAMETEMPLATE = "short";
	
	private static final String DEFAULT_NAMETEMPLATE_RESOURCE = "nameTemplateShort.json";
	
	private static final String ALTERNATE_NAMETEMPLATE = "long";
	
	private static final String ALTERNATE_NAMETEMPLATE_RESOURCE = "nameTemplateLong.json";
	
	private static final String UNKNOWN_NAMETEMPLATE = "foo";
	
	private static final int NAME_TEMPLATES_COUNT = 5;
	
	private String originalNametemplate = null;
	
	public String getURI() {
		return "nametemplate";
	}
	
	@Before
	public void before() throws Exception {
		// save a backup of the current global property value for name layout,
		// before setting a specific name layout value for the tests.
		AdministrationService service = Context.getAdministrationService();
		originalNametemplate = service.getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_LAYOUT_NAME_FORMAT);
		service.setGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_LAYOUT_NAME_FORMAT, DEFAULT_NAMETEMPLATE);
	}
	
	@After
	public void after() throws Exception {
		if (!StringUtils.isEmpty(originalNametemplate)) {
			// restore the backed up name layout global property value.
			AdministrationService service = Context.getAdministrationService();
			service.setGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_LAYOUT_NAME_FORMAT, originalNametemplate);
		}
	}
	
	@Test
	public void shouldGetAll() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI());
		SimpleObject result = deserialize(handle(req));
		
		SimpleObject defaultNameTemplateResource = parseNameTemplateResource(DEFAULT_NAMETEMPLATE_RESOURCE);
		SimpleObject alternateNameTemplateResource = parseNameTemplateResource(ALTERNATE_NAMETEMPLATE_RESOURCE);
		
		Assert.assertThat(result.containsKey("results"), is(true));
		Assert.assertThat(result.get("results"), iterableWithSize(NAME_TEMPLATES_COUNT));
		Assert.assertThat(result.get("results"), hasItem(defaultNameTemplateResource));
		Assert.assertThat(result.get("results"), hasItem(alternateNameTemplateResource));
	}
	
	@Test
	public void shouldGetGlobalNameTemplate() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI() + "/" + URL_GLOBAL_NAMETEMPLATE);
		SimpleObject result = deserialize(handle(req));
		
		SimpleObject expectedResult = withResourceVersion(parseNameTemplateResource(DEFAULT_NAMETEMPLATE_RESOURCE));
		
		Assert.assertThat(result, is(expectedResult));
	}
	
	@Test
	public void shouldGetNameTemplateByCodename() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI() + "/" + ALTERNATE_NAMETEMPLATE);
		SimpleObject result = deserialize(handle(req));
		
		SimpleObject expectedResult = withResourceVersion(parseNameTemplateResource(ALTERNATE_NAMETEMPLATE_RESOURCE));
		
		Assert.assertThat(result, is(expectedResult));
	}
	
	@Test
	public void shouldReturnNotFoundForUnknownCodename() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI() + "/" + UNKNOWN_NAMETEMPLATE);
		MockHttpServletResponse result = null;
		ObjectNotFoundException handledException = null;
		try {
			result = handle(req);
		}
		catch (ObjectNotFoundException ex) {
			handledException = ex;
		}
		Assert.assertNotNull(handledException);
		Assert.assertNull(result);
	}
	
	private static SimpleObject parseNameTemplateResource(String resourceName) throws IOException {
		String json;
		try (InputStream inputStream = NameTemplateController2_0Test.class.getClassLoader()
				.getResourceAsStream(resourceName)) {
			json = IOUtils.toString(inputStream, "UTF-8");
		}
		return SimpleObject.parseJson(json);
	}
	
	private static SimpleObject withResourceVersion(SimpleObject so) {
		return so.add("resourceVersion", RestConstants2_0.RESOURCE_VERSION);
	}
}
