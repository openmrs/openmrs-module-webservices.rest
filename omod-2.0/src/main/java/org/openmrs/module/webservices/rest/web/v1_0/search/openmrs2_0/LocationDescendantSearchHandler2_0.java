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

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
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
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
public class LocationDescendantSearchHandler2_0 implements SearchHandler {

		private final SearchConfig searchConfig = new SearchConfig("first", RestConstants.VERSION_1 + "/location",
				Arrays.asList("2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*", "2.5.*"), Arrays.asList(new SearchQuery.Builder(
				"Allows you to get all descendant locations of a given location ")
				.withRequiredParameters("parentLocation").build()));

		@Override
		public SearchConfig getSearchConfig() {
			return searchConfig;
		}

		@Override
		public PageableResult search(RequestContext context) throws ResponseException {
			List<Location> SearchResults = new ArrayList<>();
			String parentUuid = context.getParameter("parentLocation");
			if(StringUtils.isNotBlank(parentUuid)) {
				Location location = Context.getLocationService().getLocationByUuid(parentUuid);
				if (location == null) {
					location = Context.getLocationService().getLocation(parentUuid);
				}
				if(location != null) {
					Set<Location> descendants = location.getDescendantLocations(true);
					for(Location l:descendants) {
						SearchResults.add(l);
					}
					return new NeedsPaging<Location>(SearchResults, context);
				}
			}
			return new EmptySearchResult();
		}
}
