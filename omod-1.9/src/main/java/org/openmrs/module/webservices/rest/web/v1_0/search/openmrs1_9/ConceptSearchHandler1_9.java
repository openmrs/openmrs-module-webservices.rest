/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_9;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.api.ConceptService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.api.RestHelperService.Field;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Returns concepts which map to term (uuid)
 */
@Component
public class ConceptSearchHandler1_9 implements SearchHandler {
	
	@Autowired
	@Qualifier("conceptService")
	ConceptService conceptService;
	
	@Autowired
	@Qualifier("restHelperService")
	RestHelperService restHelperService;
	
	private final SearchConfig searchConfig = new SearchConfig("byTerm", RestConstants.VERSION_1 + "/concept",
			Collections.singletonList("1.9.* - 9.*"),
	        new SearchQuery.Builder(
	                "Allows you to find concepts which map to term, uuid of term given as input").withRequiredParameters(
	            "term").build());
	
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
		String uuid = context.getParameter("term");
		String source = context.getParameter("source");
		String code = context.getParameter("code");
		String name = context.getParameter("name");
		String conceptClass = context.getParameter("class");
		String searchType = context.getParameter("searchType");
		String conceptReferences = context.getParameter("references");
		
		List<Concept> concepts;

		if (StringUtils.isNotBlank(conceptReferences)) {
			String[] conceptReferenceStrings = conceptReferences.split(",");
			concepts = new ArrayList<Concept>(conceptReferenceStrings.length);

			for (String conceptReference : conceptReferenceStrings) {
				if (StringUtils.isBlank(conceptReference)) {
					continue;
				}
				// handle UUIDs
				if (RestUtil.isValidUuid(conceptReference)) {
					Concept concept = conceptService.getConceptByUuid(conceptReference);
					if (concept != null) {
						concepts.add(concept);
						continue;
					}
				}
				// handle mappings
				int idx = conceptReference.indexOf(':');
				if (idx >= 0 && idx < conceptReference.length() - 1) {
					String conceptSource = conceptReference.substring(0, idx);
					String conceptCode = conceptReference.substring(idx + 1);
					Concept concept = conceptService.getConceptByMapping(conceptCode, conceptSource, false);
					if (concept != null) {
						concepts.add(concept);
						continue;
					}
				}
			}
			if (concepts.size() == 0) {
				return new EmptySearchResult();
			}
	
			return new NeedsPaging<Concept>(concepts, context);
		}
		
		ConceptReferenceTerm conceptReferenceTerm = conceptService.getConceptReferenceTermByUuid(uuid);
		if (conceptReferenceTerm == null) {
			return new EmptySearchResult();
		}
		
		else {
			List<ConceptMap> conceptMaps = new ArrayList<ConceptMap>();
			conceptMaps.addAll(restHelperService.getObjectsByFields(ConceptMap.class, new Field("conceptReferenceTerm",
			        conceptReferenceTerm)));
			concepts = new ArrayList<Concept>();
			for (ConceptMap conceptMap : conceptMaps) {
				if (!conceptMap.getConcept().isRetired() || context.getIncludeAll()) {
					concepts.add(conceptMap.getConcept());
					
				}
			}
			return new NeedsPaging<Concept>(concepts, context);
		}
	}
	
}
