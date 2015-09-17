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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientIdentifierResource1_8;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class PatientIdentifierResource1_8Test extends BaseDelegatingResourceTest<PatientIdentifierResource1_8, PatientIdentifier> {
	
	@Override
	public PatientIdentifier newObject() {
		return Context.getService(PatientService.class).getPatientIdentifierByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("identifier", getObject().getIdentifier());
		assertPropPresent("identifierType");
		assertPropPresent("location");
		assertPropEquals("preferred", getObject().getPreferred());
		assertPropEquals("voided", getObject().getVoided());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("identifier", getObject().getIdentifier());
		assertPropPresent("identifierType");
		assertPropPresent("location");
		assertPropEquals("preferred", getObject().getPreferred());
		assertPropEquals("voided", getObject().getVoided());
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "OpenMRS Identification Number = 101-6";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_8.PATIENT_IDENTIFIER_UUID;
	}
	
}
