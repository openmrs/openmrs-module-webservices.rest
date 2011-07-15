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

import org.openmrs.module.webservices.rest.web.v1_0.resource.ObsResource;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ObsResourceTest extends BaseDelegatingResourceTest<ObsResource, Obs> {
	
	@Override
	public Obs newObject() {
		return Context.getObsService().getObsByUuid(getUuidProperty());
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		assertPropPresent("person");
		assertPropPresent("concept");
		assertPropPresent("value");
		assertPropEquals("obsDatetime", getObject().getObsDatetime());
		assertPropEquals("accessionNumber", getObject().getAccessionNumber());
		assertPropEquals("obsGroup", getObject().getObsGroup());
		assertPropPresent("groupMembers");
		assertPropEquals("comment", getObject().getComment());
		assertPropPresent("location");
		assertPropPresent("order");
		assertPropPresent("encounter");
		assertPropEquals("voided", getObject().getVoided());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		assertPropPresent("person");
		assertPropPresent("concept");
		assertPropPresent("value");
		assertPropEquals("obsDatetime", getObject().getObsDatetime());
		assertPropEquals("accessionNumber", getObject().getAccessionNumber());
		assertPropEquals("obsGroup", getObject().getObsGroup());
		assertPropPresent("groupMembers");
		assertPropEquals("comment", getObject().getComment());
		assertPropPresent("location");
		assertPropPresent("order");
		assertPropPresent("encounter");
		assertPropEquals("voided", getObject().getVoided());
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "WEIGHT (KG) = 50.0";
	}
	
	@Override
	public String getUuidProperty() {
		return ResourceTestConstants.OBS_UUID;
	}
	
}
