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

import org.openmrs.layout.name.NameSupport;
import org.openmrs.layout.name.NameTemplate;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class NameTemplateResource2_0Test extends BaseDelegatingResourceTest<NameTemplateResource2_0, NameTemplate> {

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
