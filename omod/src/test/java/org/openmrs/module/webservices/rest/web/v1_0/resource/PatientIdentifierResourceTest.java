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
package org.openmrs.module.webservices.rest.web.v1_0.resource;

import org.openmrs.module.webservices.rest.web.v1_0.resource.PatientIdentifierResource;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class PatientIdentifierResourceTest extends BaseDelegatingResourceTest<PatientIdentifierResource, PatientIdentifier> {
	
	@Override
	public PatientIdentifier newObject() {
		return Context.getService(PatientService.class).getPatientByUuid("da7f524f-27ce-4bb2-86d6-6d1d05312bd5")
		        .getPatientIdentifier();
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("display", getResource().getDisplayString(getObject()));
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("identifier", getObject().getIdentifier());
		assertContains("identifierType");
		assertContains("location");
		assertEquals("preferred", getObject().getPreferred());
		assertEquals("voided", getObject().getVoided());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("identifier", getObject().getIdentifier());
		assertContains("identifierType");
		assertContains("location");
		assertEquals("preferred", getObject().getPreferred());
		assertEquals("voided", getObject().getVoided());
		assertContains("auditInfo");
	}
	
}
