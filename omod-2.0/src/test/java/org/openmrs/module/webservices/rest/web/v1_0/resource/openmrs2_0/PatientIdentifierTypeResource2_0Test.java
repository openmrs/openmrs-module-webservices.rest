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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class PatientIdentifierTypeResource2_0Test extends BaseDelegatingResourceTest<PatientIdentifierTypeResource2_0, PatientIdentifierType> {

	@Override
	public PatientIdentifierType newObject() {
		return Context.getService(PatientService.class).getPatientIdentifierTypeByUuid(getUuidProperty());
	}

	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("format", getObject().getFormat());
		assertPropEquals("formatDescription", getObject().getFormatDescription());
		assertPropEquals("required", getObject().getRequired());
		assertPropEquals("validator", getObject().getValidator());
		assertPropPresent("locationBehavior");
		assertPropEquals("retired", getObject().getRetired());
		
		assertPropNotPresent("checkDigit");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("format", getObject().getFormat());
		assertPropEquals("formatDescription", getObject().getFormatDescription());
		assertPropEquals("required", getObject().getRequired());
		assertPropEquals("validator", getObject().getValidator());
		assertPropPresent("locationBehavior");
		assertPropEquals("retired", getObject().getRetired());
		assertPropPresent("auditInfo");
		
		assertPropNotPresent("checkDigit");
	}
	
	@Override
	public String getDisplayProperty() {
		return "OpenMRS Identification Number";
	}

	@Override
	public String getUuidProperty() {
		return RestTestConstants1_8.PATIENT_IDENTIFIER_TYPE_UUID;
	}
}
