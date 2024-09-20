/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_5;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Diagnosis;
import org.openmrs.api.DiagnosisService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.RestTestConstants2_2;

/**
 * Tests functionality of {@link DiagnosisResource2_5}.
 */
public class DiagnosisResource2_5Test extends BaseDelegatingResourceTest<DiagnosisResource2_5, Diagnosis> {


    private DiagnosisService diagnosisService;

    private EncounterService encounterService;

    private PatientService patientService;


    @Before
    public void before() {
        this.diagnosisService =  Context.getDiagnosisService();
        this.encounterService = Context.getEncounterService();
        this.patientService = Context.getPatientService();
        executeDataSet("DiagnosisResourceTestDataset.xml");
    }

    @Override
    public Diagnosis newObject() {
        return diagnosisService.getDiagnosisByUuid(getUuidProperty());
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants2_2.DIAGNOSIS_UUID;
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropEquals("formFieldNamespace", getObject().getFormFieldNamespace());
        assertPropEquals("formFieldPath", getObject().getFormFieldPath());
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropEquals("formFieldNamespace", getObject().getFormFieldNamespace());
        assertPropEquals("formFieldPath", getObject().getFormFieldPath());
    }

    @Override
    public String getDisplayProperty() {
        return "";
    }

    @Test
    public void testFormFieldNamespaceAndPath() {
        String uuid = "a303bbfb-w5w4-25d1-9f11-4f33f99d456r";
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setUuid(uuid);
        diagnosis.setEncounter(encounterService.getEncounter(1));
        diagnosis.setPatient(patientService.getPatient(1));
        diagnosis.setRank(2);

        final String NAMESPACE = "namespace";
        final String FORMFIELD_PATH = "formFieldPath";
        diagnosis.setFormField(NAMESPACE, FORMFIELD_PATH);
        diagnosisService.save(diagnosis);

        Diagnosis savedDiagnosis = diagnosisService.getDiagnosisByUuid(uuid);
        String formFieldNameSpace = savedDiagnosis.getFormFieldNamespace();
        String formFieldPath = savedDiagnosis.getFormFieldPath();

        Assert.assertEquals("namespace", formFieldNameSpace);
        Assert.assertEquals("formFieldPath", formFieldPath);
        Assert.assertNotNull(savedDiagnosis.getFormNamespaceAndPath());
    }
}
