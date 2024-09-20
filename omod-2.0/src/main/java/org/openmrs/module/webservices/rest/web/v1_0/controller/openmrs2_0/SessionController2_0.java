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

import java.util.Collection;
import java.util.HashSet;

import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9.SessionController1_9;
import org.openmrs.util.PrivilegeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @see SessionController1_9
 */
@Controller
@RequestMapping
public class SessionController2_0 extends SessionController1_9 {

    private static final Logger log = LoggerFactory.getLogger(SessionController2_0.class);

    /**
     * @see SessionController1_9#getCurrentProvider()
     */
    @Override
    protected Provider getCurrentProvider() {
        Provider currentProvider = null;
        User currentUser = Context.getAuthenticatedUser();
        if (currentUser != null) {
            Collection<Provider> providers = new HashSet<Provider>();
            try {
                Context.addProxyPrivilege(PrivilegeConstants.GET_PROVIDERS);
                if (currentUser.getPerson() != null) {
                    providers = Context.getProviderService().getProvidersByPerson(currentUser.getPerson(), false);
                }
            }
            finally {
                Context.removeProxyPrivilege(PrivilegeConstants.GET_PROVIDERS);
            }
            if (providers.size() > 1) {
                log.warn("Can't handle users with multiple provider accounts");
            } else if (providers.size() == 1) {
                currentProvider = providers.iterator().next();
            }
        }
        return currentProvider;
    }
}
