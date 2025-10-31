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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class ProviderSearchHandler2_8Test extends RestControllerTestUtils {

    @Before
    public void init() throws Exception {
        executeDataSet("providerTestDataset.xml");
        executeDataSet("providerRoleTestDataset.xml");
    }

    @Test
    public void shouldReturnProvidersBySingleRole() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, "provider");
        req.addParameter("providerRoles", "da7f523f-27ce-4bb2-86d6-6d1d05312bd5"); //binome role
        SimpleObject result = deserialize(handle(req));
        List<Provider> providers = (List<Provider>) result.get("results");
        Assert.assertEquals(1, providers.size());
    }

    @Test
    public void shouldReturnProvidersByMultipleRolesByCommaSeparatedSingleParameter() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, "provider");
        req.addParameter("providerRoles", "da7f523f-27ce-4bb2-86d6-6d1d05312bd5,ea7f523f-27ce-4bb2-86d6-6d1d05312bd5"); //binome and binome superviser roles
        SimpleObject result = deserialize(handle(req));
        List<Provider> providers = (List<Provider>) result.get("results");
        Assert.assertEquals(2, providers.size());
    }

    @Test
    public void shouldReturnProvidersByMultipleRolesByMultipleParameters() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, "provider");
        req.addParameter("providerRoles", "da7f523f-27ce-4bb2-86d6-6d1d05312bd5");
        req.addParameter("providerRoles", "ea7f523f-27ce-4bb2-86d6-6d1d05312bd5");
        SimpleObject result = deserialize(handle(req));
        List<Provider> providers = (List<Provider>) result.get("results");
        Assert.assertEquals(2, providers.size());
    }


    @Test(expected = APIException.class)
    public void shouldThrowExceptionIfInvalidRole() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, "provider");
        req.addParameter("providerRoles", "bogus");
        SimpleObject result = deserialize(handle(req));
        List<Provider> providers = (List<Provider>) result.get("results");
    }

}

