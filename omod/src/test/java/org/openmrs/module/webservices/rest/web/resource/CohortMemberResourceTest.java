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
package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.module.webservices.rest.web.v1_0.resource.CohortMemberResource;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.CohortMember;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class CohortMemberResourceTest extends BaseDelegatingResourceTest<CohortMemberResource, CohortMember> {
	
	@Override
	public CohortMember newObject() {
		Cohort cohort = Context.getCohortService().getCohortByUuid(ResourceTestConstants.COHORT_UUID);
		Patient patient = Context.getPatientService().getPatientByUuid(ResourceTestConstants.PATIENT_UUID);
		return new CohortMember(patient, cohort);
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		assertEquals("display", getResource().getDisplayString(getObject()));
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		assertContains("patient");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		assertContains("patient");
	}
}
