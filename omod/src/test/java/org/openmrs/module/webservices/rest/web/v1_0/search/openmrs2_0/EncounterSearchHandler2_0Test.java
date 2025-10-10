/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs2_0;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class EncounterSearchHandler2_0Test extends RestControllerTestUtils {

    protected String getURI() {
        return "encounter";
    }

    /**
     * @verifies returns encounters and totalCount filtered by patient uuid only
     * @see EncounterSearchHandler2_0#search(RequestContext)
     * @throws Exception
     */
    @Test
    public void search_shouldReturnEncountersWithTotalCountFilteredByPatient() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("patient", RestTestConstants1_9.PATIENT_WITH_OBS_UUID);
        req.addParameter("totalCount", String.valueOf(Boolean.TRUE));

        SimpleObject result = deserialize(handle(req));
        List<Encounter> encounters = result.get("results");
        Assert.assertEquals(3, encounters.size());
        int totalCount = result.get("totalCount");
        Assert.assertNotNull(totalCount);
        Assert.assertEquals(3, totalCount);
        Assert.assertEquals(encounters.size(), totalCount);
    }

    /**
     * @verifies returns encounters and totalCount filtered by patient uuid and encounterType uuid
     * @see EncounterSearchHandler2_0#search(RequestContext)
     * @throws Exception
     */
    @Test
    public void search_shouldReturnEncountersWithTotalCountFilteredByPatientAndEncounterType() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("patient", RestTestConstants1_9.PATIENT_WITH_OBS_UUID);
        req.addParameter("encounterType", RestTestConstants1_8.ENCOUNTER_TYPE_UUID);
        req.addParameter("totalCount", String.valueOf(Boolean.TRUE));

        SimpleObject result = deserialize(handle(req));
        List<Encounter> encounters = result.get("results");
        Assert.assertEquals(2, encounters.size());
        int totalCount = result.get("totalCount");
        Assert.assertNotNull(totalCount);
        Assert.assertEquals(2, totalCount);
        Assert.assertEquals(encounters.size(), totalCount);
    }

    /**
     * @verifies returns encounters and totalCount filtered by patient uuid and limit
     * i.e. (limit 1, results size should not be the same as totalCount)
     *
     * @see EncounterSearchHandler2_0#search(RequestContext)
     * @throws Exception
     */
    @Test
    public void search_shouldReturnEncountersWithTotalCountFilteredByPatientLimitedToOne() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("patient", RestTestConstants1_9.PATIENT_WITH_OBS_UUID);
        req.addParameter("limit", String.valueOf(1));
        req.addParameter("totalCount", String.valueOf(Boolean.TRUE));

        SimpleObject result = deserialize(handle(req));
        List<Encounter> encounters = result.get("results");
        Assert.assertEquals(1, encounters.size());
        int totalCount = result.get("totalCount");
        Assert.assertNotNull(totalCount);
        Assert.assertEquals(3, totalCount);
        Assert.assertNotEquals(encounters.size(), totalCount);
    }

    /**
     * @verifies returns encounters and totalCount filtered by patient uuid and limit
     * i.e. (limit 1, results size should not be the same as totalCount). Same as
     * previous test, but invokes the default SearchHandler explicitly via the URI. 
     *
     * @see EncounterSearchHandler2_0#search(RequestContext)
     * @throws Exception
     */
    @Test
    public void defaultSearchHandler_shouldReturnEncountersWithTotalCountFilteredByPatientLimitedToOne() throws Exception {
        String searchHandlerURI = getURI() + "/search/default";
        MockHttpServletRequest req = request(RequestMethod.GET, searchHandlerURI);
        req.addParameter("patient", RestTestConstants1_9.PATIENT_WITH_OBS_UUID);
        req.addParameter("limit", String.valueOf(1));
        req.addParameter("totalCount", String.valueOf(Boolean.TRUE));

        SimpleObject result = deserialize(handle(req));
        List<Encounter> encounters = result.get("results");
        Assert.assertEquals(1, encounters.size());
        int totalCount = result.get("totalCount");
        Assert.assertNotNull(totalCount);
        Assert.assertEquals(3, totalCount);
        Assert.assertNotEquals(encounters.size(), totalCount);
    }
}
