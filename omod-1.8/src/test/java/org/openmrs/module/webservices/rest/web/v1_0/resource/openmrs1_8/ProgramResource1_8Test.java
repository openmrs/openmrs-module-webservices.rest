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

import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;


public class ProgramResource1_8Test extends BaseDelegatingResourceTest<ProgramResource1_8, Program> {
    @Override
    public Program newObject() {
        return Context.getProgramWorkflowService().getProgramByUuid(getUuidProperty());
    }

    @Override
    public void validateRefRepresentation() throws Exception {
        super.validateRefRepresentation();
        assertPropEquals("retired", getObject().isRetired());
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropEquals("name", getObject().getName());
        assertPropEquals("description", getObject().getDescription());
        assertPropEquals("retired", getObject().getRetired());
        assertPropPresent("concept");
        assertPropPresent("auditInfo");
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropEquals("name", getObject().getName());
        assertPropEquals("description", getObject().getDescription());
        assertPropEquals("retired", getObject().getRetired());
        assertPropPresent("concept");
        assertPropPresent("allWorkflows");
    }

    @Override
    public String getDisplayProperty() {
        return getObject().getName();
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants1_8.PROGRAM_UUID;
    }
}
