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

import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

/**
 * Contains tests for the {@link org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.RelationShipTypeResource1_8}
 */
public class RelationshipTypeResource1_8Test extends BaseDelegatingResourceTest<RelationShipTypeResource1_8, RelationshipType> {

    @Override
    public RelationshipType newObject() {
        return Context.getPersonService().getRelationshipTypeByUuid(getUuidProperty());
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropEquals("display", getObject().toString());
        assertPropEquals("description", getObject().getDescription());
        assertPropEquals("aIsToB", getObject().getaIsToB());
        assertPropEquals("bIsToA", getObject().getbIsToA());
        assertPropEquals("retired", getObject().isRetired());
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropEquals("display", getObject().toString());
        assertPropEquals("description", getObject().getDescription());
        assertPropEquals("aIsToB", getObject().getaIsToB());
        assertPropEquals("bIsToA", getObject().getbIsToA());
        assertPropEquals("retired", getObject().isRetired());
        assertPropEquals("weight", getObject().getWeight());
        assertPropPresent("auditInfo");
    }

    @Override
    public String getDisplayProperty() {
        return getObject().toString();
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants1_8.RELATIONSHIP_TYPE_UUID;
    }

}
