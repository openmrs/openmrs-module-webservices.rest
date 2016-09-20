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

import org.openmrs.EncounterProvider;
import org.openmrs.EncounterRole;
import org.openmrs.Provider;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class EncounterProviderResource1_9Test extends BaseDelegatingResourceTest<EncounterProviderResource1_9, EncounterProvider> {
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		assertPropPresent("provider");
		assertPropPresent("encounterRole");
		assertPropEquals("encounterRole", getObject().getEncounterRole());
		assertPropEquals("provider", getObject().getProvider());
		assertPropEquals("uuid", getObject().getUuid());
	}
	
	@Override
	public EncounterProvider newObject() {
		EncounterProvider encounterProvider = new EncounterProvider();
		encounterProvider.setId(1);
		encounterProvider.setUuid(getUuidProperty());
		Provider provider = new Provider(1000);
		provider.setName("Testing Provider");
		
		EncounterRole rolePlayed = new EncounterRole(3000);
		rolePlayed.setName("Nurse");
		return encounterProvider;
	}
	
	@Override
	public String getDisplayProperty() {
		EncounterProvider encounterProvider = newObject();
		Provider provider = encounterProvider.getProvider();
		EncounterRole role = encounterProvider.getEncounterRole();
		
		if (role == null) {
			if (provider == null) {
				return null;
			}
			return provider.getName();
		}
		
		return provider.getName() + ": " + role.getName();
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_9.ENCOUNTER_PROVIDER_UUID;
	}
}
