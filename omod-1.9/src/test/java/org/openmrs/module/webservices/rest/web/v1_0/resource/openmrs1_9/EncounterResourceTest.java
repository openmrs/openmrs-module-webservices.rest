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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

/**
 * Contains tests for the {@link EncounterResource}
 */
public class EncounterResourceTest extends BaseDelegatingResourceTest<EncounterResource, Encounter> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#newObject()
	 */
	@Override
	public Encounter newObject() {
		return Context.getEncounterService().getEncounterByUuid(getUuidProperty());
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#validateDefaultRepresentation()
	 */
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("encounterDatetime", getObject().getEncounterDatetime());
		assertPropPresent("patient");
		assertPropPresent("location");
		assertPropPresent("form");
		assertPropPresent("encounterType");
		assertPropPresent("provider");
		assertPropPresent("obs");
		assertPropPresent("orders");
		assertPropEquals("voided", getObject().getVoided());
		assertPropPresent("visit");
		assertPropEquals("resourceVersion", "1.9");
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#validateFullRepresentation()
	 */
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("encounterDatetime", getObject().getEncounterDatetime());
		assertPropEquals("encounterDatetime", getObject().getEncounterDatetime());
		assertPropPresent("patient");
		assertPropPresent("location");
		assertPropPresent("form");
		assertPropPresent("encounterType");
		assertPropPresent("provider");
		assertPropPresent("obs");
		assertPropPresent("orders");
		assertPropEquals("voided", getObject().getVoided());
		assertPropPresent("auditInfo");
		assertPropPresent("visit");
		assertPropEquals("resourceVersion", "1.9");
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#getDisplayProperty()
	 */
	@Override
	public String getDisplayProperty() {
		return "Emergency 01/08/2008";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#getUuidProperty()
	 */
	@Override
	public String getUuidProperty() {
		return "6519d653-393b-4118-9c83-a3715b82d4ac";
	}
}
