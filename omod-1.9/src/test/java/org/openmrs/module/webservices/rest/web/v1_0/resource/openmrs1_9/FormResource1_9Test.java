/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.junit.Before;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class FormResource1_9Test extends BaseDelegatingResourceTest<FormResource1_9, Form> {
	
	@Before
	public void setUp() throws Exception {
		executeDataSet(RestTestConstants1_9.FORM_RESOURCE_DATA_SET);
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("retired", getObject().isRetired());
		assertPropPresent("encounterType");
		assertPropEquals("version", getObject().getVersion());
		assertPropEquals("build", getObject().getBuild());
		assertPropEquals("published", getObject().getPublished());
		assertPropPresent("formFields");
		assertPropPresent("resources");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("retired", getObject().isRetired());
		assertPropPresent("encounterType");
		assertPropEquals("version", getObject().getVersion());
		assertPropEquals("build", getObject().getBuild());
		assertPropEquals("published", getObject().getPublished());
		assertPropPresent("formFields");
		assertPropPresent("auditInfo");
		assertPropPresent("resources");
	}
	
	@Override
	public Form newObject() {
		return Context.getFormService().getFormByUuid(getUuidProperty());
	}
	
	@Override
	public String getDisplayProperty() {
		return getObject().getName();
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_9.FORM_UUID;
	}
}
