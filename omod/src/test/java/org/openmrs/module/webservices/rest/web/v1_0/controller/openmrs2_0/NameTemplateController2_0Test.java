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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.layout.name.NameSupport;
import org.openmrs.layout.name.NameTemplate;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0.RestConstants2_0;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;

import static org.openmrs.util.OpenmrsConstants.GLOBAL_PROPERTY_LAYOUT_NAME_FORMAT;
import static org.openmrs.util.OpenmrsConstants.PERSON_NAME_FORMAT_SHORT;
import static org.openmrs.util.OpenmrsConstants.PERSON_NAME_FORMAT_LONG;

public class NameTemplateController2_0Test extends RestControllerTestUtils {
	
	private static final String SHORT_NAMETEMPLATE_RESOURCE = "nameTemplateShort.json";
	
	private static final String LONG_NAMETEMPLATE_RESOURCE = "nameTemplateLong.json";
	
	private static final String UNKNOWN_NAMETEMPLATE = "foo";
	
	private static final int NAME_TEMPLATES_COUNT = 2;
	
	private String originalNametemplate = null;
	
	private List<NameTemplate> originalLayoutTemplates = null;
	
	public String getURI() {
		return "nametemplate";
	}
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void before() throws Exception {
		AdministrationService service = Context.getAdministrationService();
		originalNametemplate = service.getGlobalProperty(GLOBAL_PROPERTY_LAYOUT_NAME_FORMAT);
		service.setGlobalProperty(GLOBAL_PROPERTY_LAYOUT_NAME_FORMAT, PERSON_NAME_FORMAT_SHORT);
		
		// Core 3.0 no longer bundles pre-configured name templates, so register them for tests
		NameSupport nameSupport = NameSupport.getInstance();
		originalLayoutTemplates = nameSupport.getLayoutTemplates();
		
		NameTemplate shortTemplate = createNameTemplate("Short Name Format", PERSON_NAME_FORMAT_SHORT,
				new String[] { "givenName", "middleName", "familyName" },
				new String[] { "30", "30", "30" });
		
		NameTemplate longTemplate = createNameTemplate("Long Name Format", PERSON_NAME_FORMAT_LONG,
				new String[] { "prefix", "givenName", "middleName", "familyNamePrefix", "familyName", "familyName2", "familyNameSuffix", "degree" },
				new String[] { "5", "30", "30", "8", "25", "25", "8", "5" });
		longTemplate.setLineByLineFormat(Arrays.asList("prefix", "givenName", "middleName",
				"familyNamePrefix familyName familyName2 familyNameSuffix", "degree"));
		
		nameSupport.setLayoutTemplates(Arrays.asList(shortTemplate, longTemplate));
	}
	
	@After
	public void after() throws Exception {
		if (!StringUtils.isEmpty(originalNametemplate)) {
			AdministrationService service = Context.getAdministrationService();
			service.setGlobalProperty(GLOBAL_PROPERTY_LAYOUT_NAME_FORMAT, originalNametemplate);
		}
		NameSupport.getInstance().setLayoutTemplates(originalLayoutTemplates);
	}
	
	@Test
	public void shouldGetAll() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI());
		SimpleObject result = deserialize(handle(req));

		SimpleObject defaultNameTemplateResource = parseNameTemplateResource(SHORT_NAMETEMPLATE_RESOURCE);
		SimpleObject alternateNameTemplateResource = parseNameTemplateResource(LONG_NAMETEMPLATE_RESOURCE);

		Assert.assertThat(result.containsKey("results"), is(true));
		Assert.assertThat(result.get("results"), iterableWithSize(NAME_TEMPLATES_COUNT));
		Assert.assertThat(result.get("results"), hasItem(defaultNameTemplateResource));
		Assert.assertThat(result.get("results"), hasItem(alternateNameTemplateResource));
	}
	
	@Test
	public void shouldGetGlobalNameTemplate() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI() + "/" + GLOBAL_PROPERTY_LAYOUT_NAME_FORMAT);
		SimpleObject result = deserialize(handle(req));
		
		SimpleObject expectedResult = withResourceVersion(parseNameTemplateResource(SHORT_NAMETEMPLATE_RESOURCE));
		
		Assert.assertThat(result, is(expectedResult));
	}
	
	@Test
	public void shouldGetNameTemplateByCodename() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI() + "/" + PERSON_NAME_FORMAT_LONG);
		SimpleObject result = deserialize(handle(req));
		
		SimpleObject expectedResult = withResourceVersion(parseNameTemplateResource(LONG_NAMETEMPLATE_RESOURCE));
		
		Assert.assertThat(result, is(expectedResult));
	}
	
	@Test
	public void shouldReturnNotFoundForUnknownCodename() throws Exception {
		MockHttpServletRequest req = newGetRequest(getURI() + "/" + UNKNOWN_NAMETEMPLATE);
		thrown.expect(ObjectNotFoundException.class);
		MockHttpServletResponse result = handle(req);
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
	
	private static NameTemplate createNameTemplate(String displayName, String codeName, String[] fields, String[] sizes) {
		NameTemplate template = new NameTemplate();
		template.setDisplayName(displayName);
		template.setCodeName(codeName);
		
		Map<String, String> nameMappings = new LinkedHashMap<>();
		Map<String, String> sizeMappings = new LinkedHashMap<>();
		List<String> lineByLineFormat = new ArrayList<>();
		
		for (int i = 0; i < fields.length; i++) {
			nameMappings.put(fields[i], "PersonName." + fields[i]);
			sizeMappings.put(fields[i], sizes[i]);
			lineByLineFormat.add(fields[i]);
		}
		
		template.setNameMappings(nameMappings);
		template.setSizeMappings(sizeMappings);
		template.setLineByLineFormat(lineByLineFormat);
		return template;
	}
}
