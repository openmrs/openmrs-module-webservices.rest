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
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PersonAddressResource1_8;
import org.openmrs.PersonAddress;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class PersonAddressResource1_8Test extends BaseDelegatingResourceTest<PersonAddressResource1_8, PersonAddress> {
	
	@Override
	public PersonAddress newObject() {
		return Context.getPersonService().getPersonAddressByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("preferred", getObject().getPreferred());
		assertPropEquals("address1", getObject().getAddress1());
		assertPropEquals("address2", getObject().getAddress2());
		assertPropEquals("cityVillage", getObject().getCityVillage());
		assertPropEquals("stateProvince", getObject().getStateProvince());
		assertPropEquals("country", getObject().getCountry());
		assertPropEquals("postalCode", getObject().getPostalCode());
		assertPropEquals("countyDistrict", getObject().getCountyDistrict());
		assertPropEquals("address3", getObject().getAddress3());
		assertPropEquals("address4", getObject().getAddress4());
		assertPropEquals("address5", getObject().getAddress5());
		assertPropEquals("address6", getObject().getAddress6());
		assertPropPresent("startDate");
		assertPropPresent("endDate");
		assertPropEquals("latitude", getObject().getLatitude());
		assertPropEquals("longitude", getObject().getLongitude());
		assertPropEquals("voided", getObject().getVoided());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("preferred", getObject().getPreferred());
		assertPropEquals("address1", getObject().getAddress1());
		assertPropEquals("address2", getObject().getAddress2());
		assertPropEquals("cityVillage", getObject().getCityVillage());
		assertPropEquals("stateProvince", getObject().getStateProvince());
		assertPropEquals("country", getObject().getCountry());
		assertPropEquals("postalCode", getObject().getPostalCode());
		assertPropEquals("countyDistrict", getObject().getCountyDistrict());
		assertPropEquals("address3", getObject().getAddress3());
		assertPropEquals("address4", getObject().getAddress4());
		assertPropEquals("address5", getObject().getAddress5());
		assertPropEquals("address6", getObject().getAddress6());
		assertPropPresent("startDate");
		assertPropPresent("endDate");
		assertPropEquals("latitude", getObject().getLatitude());
		assertPropEquals("longitude", getObject().getLongitude());
		assertPropEquals("voided", getObject().getVoided());
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "1050 Wishard Blvd.";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_8.PERSON_ADDRESS_UUID;
	}
	
}
