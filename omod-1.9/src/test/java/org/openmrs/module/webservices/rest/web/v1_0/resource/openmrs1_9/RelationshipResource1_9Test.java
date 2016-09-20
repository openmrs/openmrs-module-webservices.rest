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

import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.RelationshipResource1_9;

import static org.junit.Assert.assertFalse;

/**
 * Contains tests for the
 * {@link org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.RelationshipResource1_9}
 */
public class RelationshipResource1_9Test extends BaseDelegatingResourceTest<RelationshipResource1_9, Relationship> {
	
	@Override
	public Relationship newObject() {
		return Context.getPersonService().getRelationshipByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("personA");
		assertPropPresent("relationshipType");
		assertPropPresent("personB");
		assertPropPresent("startDate");
		assertPropPresent("endDate");
		assertPropEquals("voided", getObject().isVoided());
		assertFalse("Should not expose the Patient subclass",
		    findSelfLink((SimpleObject) getRepresentation().get("personA")).contains("/patient/"));
		assertFalse("Should not expose the Patient subclass",
		    findSelfLink((SimpleObject) getRepresentation().get("personB")).contains("/patient/"));
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropPresent("personA");
		assertPropPresent("relationshipType");
		assertPropPresent("personB");
		assertPropPresent("startDate");
		assertPropPresent("endDate");
		assertPropEquals("voided", getObject().isVoided());
		assertPropPresent("auditInfo");
		assertFalse("Should not expose the Patient subclass",
		    findSelfLink((SimpleObject) getRepresentation().get("personA")).contains("/patient/"));
		assertFalse("Should not expose the Patient subclass",
		    findSelfLink((SimpleObject) getRepresentation().get("personB")).contains("/patient/"));
	}
	
	@Override
	public String getDisplayProperty() {
		return "Hippocrates is the Doctor of Horatio";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_8.RELATIONSHIP_UUID;
	}
}
