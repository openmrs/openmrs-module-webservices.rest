/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_6;

import org.junit.Before;
import org.openmrs.MedicationDispense;
import org.openmrs.api.MedicationDispenseService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.RestTestConstants2_6;

public class MedicationDispenseResource2_6Test
        extends BaseDelegatingResourceTest<MedicationDispenseResource2_6, MedicationDispense> {

	@Before
	public void before() throws Exception {
		executeDataSet(RestTestConstants2_6.MEDICATION_DISPENSE_TEST_DATA_XML);
	}

	@Override
	public MedicationDispense newObject() {
		return Context.getService(MedicationDispenseService.class)
		        .getMedicationDispenseByUuid(getUuidProperty());
	}

	@Override
	public String getDisplayProperty() {
		return "Test Drug";
	}

	@Override
	public String getUuidProperty() {
		return RestTestConstants2_6.MEDICATION_DISPENSE_UUID;
	}

	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("patient");
		assertPropPresent("concept");
		assertPropPresent("status");
		assertPropEquals("voided", getObject().getVoided());
	}

	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropPresent("patient");
		assertPropPresent("concept");
		assertPropPresent("status");
		assertPropPresent("formFieldNamespace");
		assertPropPresent("formFieldPath");
		assertPropEquals("voided", getObject().getVoided());
	}
}
