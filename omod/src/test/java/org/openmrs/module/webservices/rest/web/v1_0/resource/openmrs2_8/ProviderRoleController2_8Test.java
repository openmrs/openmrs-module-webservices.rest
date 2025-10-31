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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ProviderRole;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class ProviderRoleController2_8Test extends MainResourceControllerTest {


    @Before
    public void init() throws Exception {
        executeDataSet("providerRoleTestDataset.xml");
    }

    @Test
    public void createProviderRole_shouldCreateANewProviderRole() throws Exception {
        int before = Context.getProviderService().getAllProviderRoles(false).size();
        String json = "{ \"name\": \"Social Worker\", \"description\":\"Clinical Social Worker\" }";
        handle(newPostRequest(getURI(), json));
        Assert.assertEquals(before + 1, Context.getProviderService().getAllProviderRoles(false).size());
    }

    @Test
    public void voidProvider_shouldRetireAProvider() throws Exception {
        ProviderRole providerRole = Context.getProviderService().getProviderRoleByUuid(getUuid());
        Assert.assertFalse(providerRole.isRetired());

        MockHttpServletRequest request = request(RequestMethod.DELETE, getURI() + "/" + getUuid() );
        request.addParameter("reason", "unit test");
        handle(request);

        providerRole = Context.getProviderService().getProviderRoleByUuid(getUuid());
        Assert.assertTrue(providerRole.isRetired());
        Assert.assertEquals("unit test", providerRole.getRetireReason());
    }

    @Test
    public void shouldEditAProvider() throws Exception {
        String json = "{\"description\":\"new description\"}";
        handle(newPostRequest(getURI() + "/" + getUuid(), json));

        ProviderRole updatedProviderRole = (ProviderRole) Context.getProviderService().getProviderRoleByUuid(getUuid());
        Assert.assertEquals("new description", updatedProviderRole.getDescription());
    }

    @Override
    public String getURI() {
        return "providerrole";
    }

    @Override
    public String getUuid() {
        return "da7f523f-27ce-4bb2-86d6-6d1d05312bd5";
    }

    @Override
    public long getAllCount() {
        return Context.getProviderService().getAllProviderRoles(false).size();
    }
}
