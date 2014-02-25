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
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_10;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptSource;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Allows for finding concepts by mapping or by name
 */
@Component
public class DrugSearchByMappingHandler1_10 implements SearchHandler {
	
	@Autowired
	@Qualifier("conceptService")
	ConceptService conceptService;
	
	SearchQuery requiredCodeSearchQuery = new SearchQuery.Builder(
	        "Allows you to find drugs by code and optional source and preferred map types(comma delimited). "
	                + "Gets the best matching drug, i.e. matching the earliest ConceptMapType passed if there are "
	                + "multiple matches for the highest-priority ConceptMapType").withRequiredParameters("code")
	        .withOptionalParameters("source", "mapTypes").build();
	
	SearchQuery requiredSourceSearchQuery = new SearchQuery.Builder(
	        "Allows you to find drugs by source, optional code and preferred map types(comma delimited). "
	                + "Gets the best matching drug, i.e. matching the earliest ConceptMapType passed if there are "
	                + "multiple matches for the highest-priority ConceptMapType").withRequiredParameters("source")
	        .withOptionalParameters("code", "preferredMapTypes").build();
	
	SearchQuery requiredMapTypesSearchQuery = new SearchQuery.Builder(
	        "Allows you to find drugs by preferred map types(comma delimited), optional code and source. Gets the "
	                + "best matching drug, i.e. matching the earliest ConceptMapType passed if there are multiple"
	                + " matches for the highest-priority ConceptMapType").withRequiredParameters("preferredMapTypes")
	        .withOptionalParameters("code", "source").build();
	
	//since teh core api method requires at least code or source or map types, we need to all
	//add all 3 search queries where one is required for each to enforce it via rest
	private final SearchConfig searchConfig = new SearchConfig("default", RestConstants.VERSION_1 + "/drug",
	        Arrays.asList("1.10.*"), Arrays.asList(requiredCodeSearchQuery, requiredSourceSearchQuery,
	            requiredMapTypesSearchQuery));
	
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
		String code = context.getParameter("code");
		String sourceUuid = context.getParameter("source");
		String mapTypesUuids = context.getParameter("mapTypes");
		ConceptSource source = null;
		if (StringUtils.isNotBlank(sourceUuid)) {
			source = conceptService.getConceptSourceByUuid(sourceUuid);
			if (source == null) {
				throw new ObjectNotFoundException();
			}
		}
		List<ConceptMapType> mapTypesInOrderOfPreference = null;
		if (StringUtils.isNotBlank(mapTypesUuids)) {
			String[] uuids = StringUtils.split(mapTypesUuids, ",");
			for (String uuid : uuids) {
				ConceptMapType mapType = conceptService.getConceptMapTypeByUuid(uuid.trim());
				if (mapType == null) {
					throw new ObjectNotFoundException();
				}
				if (mapTypesInOrderOfPreference == null) {
					mapTypesInOrderOfPreference = new ArrayList<ConceptMapType>();
				}
				mapTypesInOrderOfPreference.add(mapType);
			}
		}
		
		Drug drug = conceptService.getDrugByMapping(code, source, mapTypesInOrderOfPreference, context.getIncludeAll());
		if (drug == null) {
			return new EmptySearchResult();
		}
		
		List<Drug> drugs = new ArrayList<Drug>();
		drugs.add(drug);
		return new NeedsPaging<Drug>(drugs, context);
	}
}
