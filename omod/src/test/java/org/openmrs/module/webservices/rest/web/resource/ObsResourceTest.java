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

import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ObsResourceTest extends BaseDelegatingResourceTest<ObsResource, Obs> {
	
	@Override
	public Obs newObject() {
		return Context.getObsService().getObsByUuid(ResourceTestConstants.OBS_UUID);
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("display", getResource().getDisplayString(getObject()));
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertContains("person");
		assertContains("concept");
		assertContains("value"); //no getter
		assertEquals("obsDatetime", getObject().getObsDatetime());
		assertEquals("accessionNumber", getObject().getAccessionNumber());
		assertEquals("obsGroup", getObject().getObsGroup());
		assertContains("groupMembers");
		assertEquals("comment", getObject().getComment());
		assertContains("location");
		assertContains("order");
		assertContains("encounter");
		assertEquals("voided", getObject().getVoided());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertContains("person");
		assertContains("concept");
		assertContains("value"); //no getter
		assertEquals("obsDatetime", getObject().getObsDatetime());
		assertEquals("accessionNumber", getObject().getAccessionNumber());
		assertEquals("obsGroup", getObject().getObsGroup());
		assertContains("groupMembers");
		assertEquals("comment", getObject().getComment());
		assertContains("location");
		assertContains("order");
		assertContains("encounter");
		assertEquals("voided", getObject().getVoided());
		assertContains("auditInfo");
	}
	
}
