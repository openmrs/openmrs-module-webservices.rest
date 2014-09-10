/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.openmrs.Relationship;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

import static org.junit.Assert.assertFalse;

/**
 * Contains tests for the {@link org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.RelationshipResource1_8}
 */
public class RelationshipResource1_8Test extends BaseDelegatingResourceTest<RelationshipResource1_8, Relationship> {
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
        assertPropEquals("voided", getObject().isVoided());
        assertFalse("Should not expose the Patient subclass", findSelfLink((SimpleObject) getRepresentation().get("personA")).contains("/patient/"));
        assertFalse("Should not expose the Patient subclass", findSelfLink((SimpleObject) getRepresentation().get("personB")).contains("/patient/"));
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropPresent("personA");
        assertPropPresent("relationshipType");
        assertPropPresent("personB");
        assertPropEquals("voided", getObject().isVoided());
        assertPropPresent("auditInfo");
        assertFalse("Should not expose the Patient subclass", findSelfLink((SimpleObject) getRepresentation().get("personA")).contains("/patient/"));
        assertFalse("Should not expose the Patient subclass", findSelfLink((SimpleObject) getRepresentation().get("personB")).contains("/patient/"));
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
