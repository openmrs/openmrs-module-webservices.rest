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

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_9.CohortMember1_9;

public class CohortMemberResource1_9Test extends BaseDelegatingResourceTest<CohortMemberResource1_9, CohortMember1_9> {
	
	@Override
	public CohortMember1_9 newObject() {
		Cohort cohort = Context.getCohortService().getCohortByUuid(RestTestConstants1_9.COHORT_UUID);
		Patient patient = Context.getPatientService().getPatientByUuid(RestTestConstants1_9.PATIENT_UUID);
		return new CohortMember1_9(patient, cohort);
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
