/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_2;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

public class EncounterController2_2Test  extends MainResourceControllerTest {

    @Override
    public String getURI() {
        return "encounter";
    }

    /**
     * @see MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        return RestTestConstants1_8.ENCOUNTER_UUID;
    }


    @Override
    public long getAllCount() {
        Map<Integer, List<Encounter>> allPatientEncounters = Context.getEncounterService().getAllEncounters(null);
        int totalEncounters = 0;
        for (Integer integer : allPatientEncounters.keySet()) {
            List<Encounter> encounters = allPatientEncounters.get(integer);
            if (encounters != null) {
                totalEncounters = totalEncounters + encounters.size();
            }
        }
        return totalEncounters;
    }

    @Override
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void shouldGetAll() throws Exception {
        super.shouldGetAll();
    }

    @Test
    public void getEncounter_shouldExcludeVoidedDiagnosis() throws Exception {
        executeDataSet("patientDiagnosisDataSet.xml"); // test data has single patient with 2 non-voided and 1 voided diagnoses
        MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/6519d653-393b-4118-9c83-a3715b82d4ac");  // encounter 3 from standard test dataset
        req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
        MockHttpServletResponse response = handle(req);
        SimpleObject result = deserialize(response);

        Util.log("full", result);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.get("diagnoses"));
        Assert.assertEquals(2, ((List<?>) result.get("diagnoses")).size());
    }
}
