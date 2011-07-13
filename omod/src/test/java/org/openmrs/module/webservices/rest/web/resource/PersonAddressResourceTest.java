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

import org.openmrs.PersonAddress;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class PersonAddressResourceTest extends BaseDelegatingResourceTest<PersonAddressResource, PersonAddress> {
	
	@Override
	public PersonAddress newObject() {
		return Context.getPersonService().getPersonByUuid(ResourceTestConstants.PERSON_UUID).getPersonAddress();
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("display", getResource().getDisplayString(getObject()));
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("preferred", getObject().getPreferred());
		assertEquals("address1", getObject().getAddress1());
		assertEquals("address2", getObject().getAddress2());
		assertEquals("cityVillage", getObject().getCityVillage());
		assertEquals("stateProvince", getObject().getStateProvince());
		assertEquals("country", getObject().getCountry());
		assertEquals("postalCode", getObject().getPostalCode());
		assertEquals("countyDistrict", getObject().getCountyDistrict());
		assertEquals("address3", getObject().getAddress3());
		assertEquals("address4", getObject().getAddress4());
		assertEquals("address5", getObject().getAddress5());
		assertEquals("address6", getObject().getAddress6());
		assertContains("startDate"); //no getter
		assertContains("endDate"); //no getter
		assertEquals("latitude", getObject().getLatitude());
		assertEquals("longitude", getObject().getLongitude());
		assertEquals("voided", getObject().getVoided());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("preferred", getObject().getPreferred());
		assertEquals("address1", getObject().getAddress1());
		assertEquals("address2", getObject().getAddress2());
		assertEquals("cityVillage", getObject().getCityVillage());
		assertEquals("stateProvince", getObject().getStateProvince());
		assertEquals("country", getObject().getCountry());
		assertEquals("postalCode", getObject().getPostalCode());
		assertEquals("countyDistrict", getObject().getCountyDistrict());
		assertEquals("address3", getObject().getAddress3());
		assertEquals("address4", getObject().getAddress4());
		assertEquals("address5", getObject().getAddress5());
		assertEquals("address6", getObject().getAddress6());
		assertContains("startDate"); //no getter
		assertContains("endDate"); //no getter
		assertEquals("latitude", getObject().getLatitude());
		assertEquals("longitude", getObject().getLongitude());
		assertEquals("voided", getObject().getVoided());
		assertContains("auditInfo");
	}
	
}
