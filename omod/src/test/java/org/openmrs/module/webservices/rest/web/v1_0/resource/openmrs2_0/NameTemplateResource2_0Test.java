/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.layout.name.NameSupport;
import org.openmrs.layout.name.NameTemplate;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.util.OpenmrsConstants.GLOBAL_PROPERTY_LAYOUT_NAME_FORMAT;
import static org.openmrs.util.OpenmrsConstants.PERSON_NAME_FORMAT_SHORT;

public class NameTemplateResource2_0Test extends BaseDelegatingResourceTest<NameTemplateResource2_0, NameTemplate> {

	@Before
	public void setUp() {
		// Core 3.0 no longer bundles pre-configured name templates, so register one for tests
		NameTemplate shortTemplate = new NameTemplate();
		shortTemplate.setDisplayName("Short Name Format");
		shortTemplate.setCodeName(PERSON_NAME_FORMAT_SHORT);
		Map<String, String> nameMappings = new LinkedHashMap<>();
		nameMappings.put("givenName", "PersonName.givenName");
		nameMappings.put("middleName", "PersonName.middleName");
		nameMappings.put("familyName", "PersonName.familyName");
		shortTemplate.setNameMappings(nameMappings);
		Map<String, String> sizeMappings = new LinkedHashMap<>();
		sizeMappings.put("givenName", "30");
		sizeMappings.put("middleName", "30");
		sizeMappings.put("familyName", "30");
		shortTemplate.setSizeMappings(sizeMappings);
		shortTemplate.setLineByLineFormat(Arrays.asList("givenName", "middleName", "familyName"));

		NameSupport.getInstance().setLayoutTemplates(Arrays.asList(shortTemplate));
		Context.getAdministrationService().setGlobalProperty(GLOBAL_PROPERTY_LAYOUT_NAME_FORMAT, PERSON_NAME_FORMAT_SHORT);
	}

	@Override
	public NameTemplate newObject() {
		return NameSupport.getInstance().getDefaultLayoutTemplate();
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		/* NameTemplate resource handles all representations identically */
		validateDefaultRepresentation();
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		/* NameTemplate resource handles all representations identically */
		validateDefaultRepresentation();
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		assertPropEquals("codeName", getObject().getCodeName());
		assertPropEquals("displayName", getObject().getDisplayName());
		assertPropEquals("country", getObject().getCountry());
		assertPropEquals("lines", getObject().getLines());
		assertPropEquals("lineByLineFormat", getObject().getLineByLineFormat());
		assertPropEquals("nameMappings", getObject().getNameMappings());
		assertPropEquals("sizeMappings", getObject().getSizeMappings());
		assertPropEquals("elementDefaults", getObject().getElementDefaults());
		assertPropEquals("elementRegex", getObject().getElementRegex());
		assertPropEquals("elementRegexFormats", getObject().getElementRegexFormats());
		assertPropEquals("requiredElements", getObject().getRequiredElements());
	}
	
	@Override
	public String getDisplayProperty() {
		return null;
	}
	
	@Override
	public String getUuidProperty() {
		return "codeName";
	}
}
