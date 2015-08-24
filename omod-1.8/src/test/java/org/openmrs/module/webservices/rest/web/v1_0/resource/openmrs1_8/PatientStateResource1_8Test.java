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

import org.openmrs.PatientState;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;


public class PatientStateResource1_8Test extends BaseDelegatingResourceTest <PatientStateResource1_8,PatientState>{

    @Override
    public PatientState newObject() {
        return Context.getProgramWorkflowService().getPatientStateByUuid(getUuidProperty());
    }

    @Override
    public void validateRefRepresentation() throws Exception {
        assertPropEquals("startDate",getObject().getStartDate());
        assertPropEquals("endDate",getObject().getEndDate());
        assertPropPresent("state");
        assertPropEquals("uuid",getObject().getUuid());
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        assertPropPresent("patientProgram");
        validateRefRepresentation();
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        validateRefRepresentation();
        assertPropPresent("patientProgram");
        assertPropEquals("voided",getObject().getVoided());
        assertPropPresent("auditInfo");
    }

    @Override
    public String getDisplayProperty() {
        return getObject().getState().getConcept().getDisplayString();
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants1_8.PATIENT_STATE_UUID;
    }
}