/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs3_0;

import java.util.ArrayList;
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
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.stereotype.Component;

/**
 * Returns the locations the authenticated user is allowed to use for a given tag, so that a client
 * can populate a location picker without needing the GET_USERS privilege that reading another
 * resource's user would require.
 */
@Component
public class LocationForCurrentUserSearchHandler3_0 implements SearchHandler {
	
	public static final String FOR_CURRENT_USER = "forCurrentUser";
	
	public static final String TAG = "tag";
	
	private final SearchConfig searchConfig = new SearchConfig("locationForCurrentUser", RestConstants.VERSION_1
	        + "/location", "3.0.* - 9.*", new SearchQuery.Builder(
	        "Allows you to find the locations the authenticated user is allowed to use for a given tag")
	        .withRequiredParameters(FOR_CURRENT_USER, TAG).build());
	
	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}
	
	@Override
	public PageableResult search(RequestContext context) throws ResponseException {
		boolean forCurrentUser = Boolean.parseBoolean(context.getParameter(FOR_CURRENT_USER));
		if (forCurrentUser && Context.getAuthenticatedUser() == null) {
			return new EmptySearchResult();
		}
		
		String tag = context.getParameter(TAG);
		
		try {
			Context.addProxyPrivilege(PrivilegeConstants.GET_LOCATIONS);
			
			LocationTag locationTag = Context.getLocationService().getLocationTagByUuid(tag);
			if (locationTag == null) {
				locationTag = Context.getLocationService().getLocationTagByName(tag);
			}
			if (locationTag == null) {
				return new EmptySearchResult();
			}
			
			List<Location> locations = forCurrentUser ? new ArrayList<>(Context.getUserService()
			        .getAllowedLocationsByTag(Context.getAuthenticatedUser(), locationTag)) : Context
			        .getLocationService().getLocationsByTag(locationTag);
			
			return new NeedsPaging<Location>(locations, context);
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.GET_LOCATIONS);
		}
	}
}
