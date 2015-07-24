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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.junit.Before;
import org.openmrs.EncounterRole;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class EncounterRoleResource1_9Test extends BaseDelegatingResourceTest<EncounterRoleResource1_9, EncounterRole> {

    @Before
    public void before() throws Exception {
        executeDataSet(RestTestConstants1_9.TEST_DATASET);
    }

    @Override
    public EncounterRole newObject() {
        return Context.getEncounterService().getEncounterRoleByUuid(getUuidProperty());
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropEquals("name", getObject().getName());
        assertPropEquals("retired", getObject().getRetired());
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropPresent("description");
        assertPropEquals("name",getObject().getName());
        assertPropEquals("retired", getObject().getRetired());
        assertPropPresent("auditInfo");
    }

    @Override
    public String getDisplayProperty() {
        return "Unknown";
    }

    @Override
    public String getUuidProperty() {
        return EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID;
    }
}

