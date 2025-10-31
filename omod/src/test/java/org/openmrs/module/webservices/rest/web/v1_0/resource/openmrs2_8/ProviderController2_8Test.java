/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_8;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RequestMethod;


public class ProviderController2_8Test extends MainResourceControllerTest {

    @Before
    public void init() throws Exception {
        executeDataSet("providerRoleTestDataset.xml");
    }

    @Test
    public void getProviderByUuidShouldIncludeRoleProperty() throws Exception {
        MockHttpServletResponse response = handle(request(RequestMethod.GET, getURI() + "/" + getUuid()));
        SimpleObject result = deserialize(response);

        Assert.assertNotNull(result);
        Assert.assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
        Assert.assertEquals("Binome", PropertyUtils.getProperty(result, "providerRole.display"));
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        return "provider";
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        return "c23409f7-45d0-4e6f-a1e9-a8b6d64c055b";
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
     */
    @Override
    public long getAllCount() {
        return Context.getProviderService().getAllProviders(false).size();
    }


}
