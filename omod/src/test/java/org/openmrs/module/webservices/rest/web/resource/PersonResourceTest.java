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

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class PersonResourceTest extends BaseDelegatingResourceTest<PersonResource, Person> {
	
	@Override
	public Person newObject() {
		return Context.getPersonService().getPersonByUuid(ResourceTestConstants.PERSON_UUID);
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("display", getResource().getDisplayString(getObject()));
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("gender", getObject().getGender());
		assertEquals("age", getObject().getAge());
		assertEquals("birthdate", getObject().getBirthdate());
		assertEquals("birthdateEstimated", getObject().getBirthdateEstimated());
		assertEquals("dead", getObject().getDead());
		assertEquals("deathDate", getObject().getDeathDate());
		assertContains("causeOfDeath");
		assertContains("preferredName");
		assertContains("preferredAddress");
		assertContains("attributes");
		assertEquals("voided", getObject().getVoided());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("gender", getObject().getGender());
		assertEquals("age", getObject().getAge());
		assertEquals("birthdate", getObject().getBirthdate());
		assertEquals("birthdateEstimated", getObject().getBirthdateEstimated());
		assertEquals("dead", getObject().getDead());
		assertEquals("deathDate", getObject().getDeathDate());
		assertContains("causeOfDeath");
		assertContains("preferredName");
		assertContains("preferredAddress");
		assertContains("names");
		assertContains("addresses");
		assertContains("attributes");
		assertEquals("voided", getObject().getVoided());
		assertContains("auditInfo");
	}
	
}
