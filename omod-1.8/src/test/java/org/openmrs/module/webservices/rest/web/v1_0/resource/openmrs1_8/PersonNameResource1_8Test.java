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

import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PersonNameResource1_8;

public class PersonNameResource1_8Test extends BaseDelegatingResourceTest<PersonNameResource1_8, PersonName> {
	
	@Override
	public PersonName newObject() {
		return Context.getPersonService().getPersonNameByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("givenName", getObject().getGivenName());
		assertPropEquals("middleName", getObject().getMiddleName());
		assertPropEquals("familyName", getObject().getFamilyName());
		assertPropEquals("familyName2", getObject().getFamilyName2());
		assertPropEquals("voided", getObject().getVoided());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("givenName", getObject().getGivenName());
		assertPropEquals("middleName", getObject().getMiddleName());
		assertPropEquals("familyName", getObject().getFamilyName());
		assertPropEquals("familyName2", getObject().getFamilyName2());
		assertPropEquals("preferred", getObject().getPreferred());
		assertPropEquals("prefix", getObject().getPrefix());
		assertPropEquals("familyNamePrefix", getObject().getFamilyNamePrefix());
		assertPropEquals("familyNameSuffix", getObject().getFamilyNameSuffix());
		assertPropEquals("degree", getObject().getDegree());
		assertPropEquals("voided", getObject().getVoided());
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "Mr. Horatio Test Hornblower Esq.";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_8.PERSON_NAME_UUID;
	}
	
}
