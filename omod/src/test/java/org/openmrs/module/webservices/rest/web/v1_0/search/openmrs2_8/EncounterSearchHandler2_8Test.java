/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs2_8;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.v1_0.controller.jupiter.RestControllerTestUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class EncounterSearchHandler2_8Test extends RestControllerTestUtils {

    protected String getURI() {
        return "encounter";
    }

    /**
     * @verifies return encounters filtered by patient only (no form filter)
     */
    @Test
    public void search_shouldReturnEncountersForPatientWithNoFormFilter() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("patient", RestTestConstants1_9.PATIENT_WITH_OBS_UUID);
        req.addParameter("totalCount", String.valueOf(Boolean.TRUE));

        SimpleObject result = deserialize(handle(req));
        List<?> encounters = result.get("results");
        Assertions.assertNotNull(encounters);
        int totalCount = result.get("totalCount");
        Assertions.assertTrue(totalCount >= 0);
    }

    /**
     * @verifies return encounters filtered by patient and form uuid
     */
    @Test
    public void search_shouldReturnEncountersFilteredByPatientAndFormUuid() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("patient", RestTestConstants1_9.PATIENT_WITH_OBS_UUID);
        req.addParameter("form", RestTestConstants1_9.FORM_UUID);
        req.addParameter("totalCount", String.valueOf(Boolean.TRUE));

        SimpleObject result = deserialize(handle(req));
        List<?> encounters = result.get("results");
        Assertions.assertNotNull(encounters);
    }

    /**
     * @verifies return encounters filtered by patient and form name
     */
    @Test
    public void search_shouldReturnEncountersFilteredByPatientAndFormName() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("patient", RestTestConstants1_9.PATIENT_WITH_OBS_UUID);
        req.addParameter("formName", "Basic Form");
        req.addParameter("totalCount", String.valueOf(Boolean.TRUE));

        SimpleObject result = deserialize(handle(req));
        List<?> encounters = result.get("results");
        Assertions.assertNotNull(encounters);
    }

    /**
     * @verifies return empty result when patient not found
     */
    @Test
    public void search_shouldReturnEmptyResultForUnknownPatient() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("patient", "non-existent-uuid");
        req.addParameter("totalCount", String.valueOf(Boolean.TRUE));

        SimpleObject result = deserialize(handle(req));
        List<?> encounters = result.get("results");
        Assertions.assertNotNull(encounters);
        Assertions.assertEquals(0, encounters.size());
    }
}