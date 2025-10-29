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

import org.openmrs.Provider;
import org.openmrs.ProviderRole;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ProviderSearchHandler2_8 implements SearchHandler {

    protected final String PROVIDER_ROLES_PARAM = "providerRoles";

    private final SearchConfig searchConfig = new SearchConfig("providerByRole", RestConstants.VERSION_1 + "/provider",
            Collections.singletonList("2.8.* - 9.*"),
            new SearchQuery.Builder(
                    "Allows you to find providers by provider role uuid").withRequiredParameters(PROVIDER_ROLES_PARAM).build());

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSearchConfig()
     */
    @Override
    public SearchConfig getSearchConfig() {
        return searchConfig;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#search(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public PageableResult search(RequestContext context) throws ResponseException {

        String[] providerRoleUuidArray = context.getRequest().getParameterValues(PROVIDER_ROLES_PARAM);
        List<ProviderRole> providerRoles = new ArrayList<ProviderRole>();

        // supports both providerRoles=uuid1,uuid2 and providerRoles=uuid1&providerRoles=uuid2
        for (String providerRoleUuidString : providerRoleUuidArray) {
            for (String providerRoleUuid : providerRoleUuidString.split(",")) {
                ProviderRole providerRole = Context.getProviderService().getProviderRoleByUuid(providerRoleUuid);
                if (providerRole != null) {
                    providerRoles.add(providerRole);
                } else {
                    throw new APIException("Unable to find provider role with uuid: " + providerRoleUuid);
                }
            }
        }

        List<Provider> providers = Context.getProviderService().getProvidersByRoles(providerRoles);
        return new NeedsPaging<Provider>(providers, context);
    }
}
