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

import org.junit.Before;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

import static org.junit.Assert.assertEquals;

public class PersonAttributeTypeResourceWithConcept1_8Test extends BaseDelegatingResourceTest<PersonAttributeTypeResource1_8, PersonAttributeType> {

    private static final String ACTIVE_LIST_INITIAL_XML = "personAttributeTypeWithConcept.xml";

    @Before
    public void init() throws Exception {
        executeDataSet(ACTIVE_LIST_INITIAL_XML);
    }

    @Override
	public PersonAttributeType newObject() {
		return Context.getPersonService().getPersonAttributeTypeByUuid(getUuidProperty());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
        SimpleObject concept = (SimpleObject) getRepresentation().get("concept");
        assertEquals("d102c80f-1yz9-4da3-bb88-8122ce8868dd", concept.get("uuid"));
    }
	
	@Override
	public String getDisplayProperty() {
		return "Caste";
	}
	
	@Override
	public String getUuidProperty() {
		return "55e6ce9e-25bf-11e3-a013-3c0754156a5d";
	}
}
