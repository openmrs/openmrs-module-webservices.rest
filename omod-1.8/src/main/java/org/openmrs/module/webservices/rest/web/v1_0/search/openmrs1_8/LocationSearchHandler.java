/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.LocationTag;
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

@Component
public class LocationSearchHandler implements SearchHandler {

	private static final String VIEW_LOCATIONS = "View Locations";
	
    private final SearchConfig searchConfig = new SearchConfig("default", RestConstants.VERSION_1 + "/location", Arrays.asList(
            "1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"), new SearchQuery.Builder("Allows you to find locations by tag uuid or tag name").withRequiredParameters("tag")
            .build());

    /**
     * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSearchConfig()
     * @should return location by tag uuid
     * @should return location by tag name
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
        String tag = context.getRequest().getParameter("tag");

        List<Location> locations = new ArrayList<Location>();
        try {
            Context.addProxyPrivilege(VIEW_LOCATIONS); //Not using PrivilegeConstants.VIEW_LOCATIONS which was removed in platform 1.11+
            Context.addProxyPrivilege("Get Locations"); //1.11+

            LocationTag locationTag = Context.getLocationService().getLocationTagByUuid(tag);
            if (locationTag == null) {
                locationTag = Context.getLocationService().getLocationTagByName(tag);
            }

            if (locationTag != null) {
                locations = Context.getLocationService().getLocationsByTag(locationTag);
            }
        }
        finally {
            Context.removeProxyPrivilege(VIEW_LOCATIONS); //Not using PrivilegeConstants.VIEW_LOCATIONS which was removed in platform 1.11+
            Context.removeProxyPrivilege("Get Locations"); //1.11+
        }


        return new NeedsPaging<Location>(locations, context);
    }
}