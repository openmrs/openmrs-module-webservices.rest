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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_0;

import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.RestTestConstants2_0;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0.DrugResource2_0;

public class DrugResource2_0Test extends BaseDelegatingResourceTest<DrugResource2_0, Drug> {
	
	@Override
	public Drug newObject() {
		return Context.getConceptService().getDrugByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropNotPresent("doseStrength");
		assertPropNotPresent("units");
		assertPropNotPresent("route");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropNotPresent("doseStrength");
		assertPropNotPresent("units");
		assertPropNotPresent("route");
	}
	
	@Override
	public String getDisplayProperty() {
		return "ASPIRIN";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants2_0.DRUG_UUID;
	}
}
