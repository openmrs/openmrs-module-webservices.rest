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

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.CohortMember1_8;

public class CohortMemberResource1_8Test extends BaseDelegatingResourceTest<CohortMemberResource1_8, CohortMember1_8> {
	
	@Override
	public CohortMember1_8 newObject() {
		Cohort cohort = Context.getCohortService().getCohortByUuid(RestTestConstants1_8.COHORT_UUID);
		Patient patient = Context.getPatientService().getPatientByUuid(RestTestConstants1_8.PATIENT_UUID);
		return new CohortMember1_8(patient, cohort);
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("patient");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropPresent("patient");
	}
	
	@Override
	public String getDisplayProperty() {
		return "101-6 - Mr. Horatio Test Hornblower Esq.";
	}
	
	@Override
	public String getUuidProperty() {
		return null;
	}
}
