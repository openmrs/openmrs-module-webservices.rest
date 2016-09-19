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
 * Allows finding a drug by mapping
 */
@Component
public class DrugSearchByMappingHandler1_10 implements SearchHandler {
	
	public static final String REQUEST_PARAM_CODE = "code";
	
	public static final String REQUEST_PARAM_SOURCE = "source";
	
	public static final String REQUEST_PARAM_MAP_TYPES = "preferredMapTypes";
	
	@Autowired
	@Qualifier("conceptService")
	ConceptService conceptService;
	
	SearchQuery searchQuery = new SearchQuery.Builder(
	        "Allows you to find a drug by source, code and preferred map types(comma delimited). "
	                + "Gets the best matching drug, i.e. matching the earliest ConceptMapType passed if there are "
	                + "multiple matches for the highest-priority ConceptMapType")
	        .withRequiredParameters(REQUEST_PARAM_SOURCE)
	        .withOptionalParameters(REQUEST_PARAM_CODE, REQUEST_PARAM_MAP_TYPES).build();
	
	private final SearchConfig searchConfig = new SearchConfig("getDrugByMapping", RestConstants.VERSION_1 + "/drug",
	        Arrays.asList("1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"), searchQuery);
	
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
		String code = context.getParameter(REQUEST_PARAM_CODE);
		String sourceUuid = context.getParameter(REQUEST_PARAM_SOURCE);
		String mapTypesUuids = context.getParameter(REQUEST_PARAM_MAP_TYPES);
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
		
		Drug drug = conceptService.getDrugByMapping(code, source, mapTypesInOrderOfPreference);
		if (drug == null) {
			return new EmptySearchResult();
		}
		
		List<Drug> drugs = new ArrayList<Drug>();
		drugs.add(drug);
		return new NeedsPaging<Drug>(drugs, context);
	}
}
