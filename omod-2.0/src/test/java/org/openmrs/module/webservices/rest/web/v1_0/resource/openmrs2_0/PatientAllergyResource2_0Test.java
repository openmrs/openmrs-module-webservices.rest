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

import org.junit.Before;
import org.openmrs.Allergy;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.v1_0.RestTestConstants2_0;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class PatientAllergyResource2_0Test extends BaseDelegatingResourceTest<PatientAllergyResource2_0, Allergy> {
	
	@Before
	public void init() throws Exception {
		executeDataSet(RestTestConstants2_0.ALLERGY_TEST_DATA_XML);
	}
	
	@Override
	public Allergy newObject() {
		return Context.getPatientService().getAllergyByUuid(RestTestConstants2_0.ALLERGY_UUID);
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation(); // allergy does not have uuid so this fails
		assertPropPresent("allergen");
		assertPropPresent("severity");
		assertPropPresent("reactions");
		assertPropPresent("patient");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation(); // allergy does not have uuid so this fails
		assertPropPresent("allergen");
		assertPropPresent("severity");
		assertPropEquals("comment", getObject().getComment());
		assertPropPresent("reactions");
		assertPropPresent("patient");
		assertPropEquals("voided", getObject().getVoided());
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "STAVUDINE LAMIVUDINE AND NEVIRAPINE";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants2_0.ALLERGY_UUID;
	}
	
}
