/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_0;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

/**
 * Tests functionality of {@link SessionController2_0}
 */
public class SessionController2_0Test extends BaseModuleWebContextSensitiveTest {

    /**
     * @see SessionController2_0#get()
     * @verifies return the session with current provider if the user doesn't have Get Providers privilege
     */
    @Test
    public void get_shouldReturnCurrentProviderIfTheUserDoesNotHaveGetProvidersPrivilege() throws Exception {
        executeDataSet("sessionControllerTestDataset.xml");

        // authenticate new user without privileges
        Context.logout();
        Context.authenticate("test_user", "test");
        Assert.assertTrue(Context.isAuthenticated());

        SessionController2_0 controller = Context.getRegisteredComponents(SessionController2_0.class).iterator().next();

        Object ret = controller.get();
        Object currentProvider = PropertyUtils.getProperty(ret, "currentProvider");
        Assert.assertNotNull(currentProvider);
        Assert.assertTrue(currentProvider.toString().contains("Test Provider"));
    }
}
