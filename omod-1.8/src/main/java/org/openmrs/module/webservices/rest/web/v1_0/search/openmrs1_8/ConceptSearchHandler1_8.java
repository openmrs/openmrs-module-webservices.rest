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

import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Allows for finding concepts by mapping.
 */
@Component
public class ConceptSearchHandler1_8 implements SearchHandler {
	
	@Autowired
	@Qualifier("conceptService")
	ConceptService conceptService;
	
	private final SearchConfig searchConfig = new SearchConfig("default", "concept", Arrays.asList("1.8.*", "1.9.*"),
	        new SearchQuery.Builder("Allows you to find concepts by source and code").withRequiredParameters("source")
	                .withOptionalParameters("code").build());
	
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
		String source = context.getParameter("source");
		String code = context.getParameter("code");
		
		ConceptSource conceptSource = conceptService.getConceptSourceByUuid(source);
		if (conceptSource == null) {
			conceptSource = conceptService.getConceptSourceByName(source);
		}
		if (conceptSource == null) {
			return new EmptySearchResult();
		}
		
		if (code == null) {
			List<ConceptMap> conceptMaps = conceptService.getConceptsByConceptSource(conceptSource);
			List<Concept> concepts = new ArrayList<Concept>();
			for (ConceptMap conceptMap : conceptMaps) {
				if (!conceptMap.getConcept().isRetired() || context.getIncludeAll()) {
					concepts.add(conceptMap.getConcept());
				}
			}
			return new NeedsPaging<Concept>(concepts, context);
		} else {
			List<Concept> conceptsByMapping = conceptService.getConceptsByMapping(code, source, false);
			
			return new NeedsPaging<Concept>(conceptsByMapping, context);
		}
	}
	
}
