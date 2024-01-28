/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class EncounterSearchHandlerTest2_4  extends RestControllerTestUtils {

    private static final String ENCOUNTER_TEST_INITIAL_XML = "encounterTestDataset.xml";

    @Before
    public void init() throws Exception {
        service = Context.getEncounterService();
        executeDataSet(ENCOUNTER_TEST_INITIAL_XML);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
     */

    protected String getURI() {
        return "encounter";
    }

    @Test
    public void searchEncounter_shouldReturnEncounterForAPatient() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("s", "byEncounterForms");
        req.addParameter("patient", "41c6b35e-c093-11e3-be87-005056821db0");

        SimpleObject result = deserialize(handle(req));
        List<Object> hits = result.get("results");
        Assert.assertEquals(3, hits.size());
    }
}
