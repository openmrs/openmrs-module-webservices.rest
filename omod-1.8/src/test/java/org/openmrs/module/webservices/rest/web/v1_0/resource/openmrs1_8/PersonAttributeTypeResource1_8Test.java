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
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class PersonAttributeTypeResource1_8Test extends BaseDelegatingResourceTest<PersonAttributeTypeResource1_8, PersonAttributeType> {

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
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("format", getObject().getFormat());
		assertPropEquals("foreignKey", getObject().getForeignKey());
		assertPropEquals("sortWeight", getObject().getSortWeight());
		assertPropEquals("searchable", getObject().getSearchable());
		assertPropEquals("editPrivilege", getObject().getEditPrivilege());
		assertPropEquals("retired", getObject().getRetired());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("format", getObject().getFormat());
		assertPropEquals("foreignKey", getObject().getForeignKey());
		assertPropEquals("sortWeight", getObject().getSortWeight());
		assertPropEquals("searchable", getObject().getSearchable());
		assertPropEquals("editPrivilege", getObject().getEditPrivilege());
		assertPropEquals("retired", getObject().getRetired());
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "Race";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_8.PERSON_ATTRIBUTE_TYPE_UUID;
	}
}
