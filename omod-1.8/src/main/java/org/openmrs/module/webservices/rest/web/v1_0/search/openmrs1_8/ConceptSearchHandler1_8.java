/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_8;

import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSearchResult;
import org.openmrs.ConceptSource;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.LocaleUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Allows for finding concepts by mapping or by name
 */
@Component
public class ConceptSearchHandler1_8 implements SearchHandler {
	
	@Autowired
	@Qualifier("conceptService")
	ConceptService conceptService;
	
	private final SearchConfig searchConfig = new SearchConfig("default", RestConstants.VERSION_1 + "/concept",
	        Arrays.asList("1.8.* - 9.*"),
	        Arrays.asList(
	            new SearchQuery.Builder("Allows you to find concepts by source and code")
					.withRequiredParameters("source")
					.withOptionalParameters("code")
					.build(),
				new SearchQuery.Builder("Allows you to find concepts by name and class")
					.withOptionalParameters("class", "name", "searchType")
					.build()
			));
	
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
		String name = context.getParameter("name");
		String conceptClass = context.getParameter("class");
		String searchType = context.getParameter("searchType");

		// filter results by name
		if ( name != null ) {
			// optional filter by the class
			ConceptClass conceptClassResult = conceptService.getConceptClassByUuid(conceptClass);
			if ("fuzzy".equals(searchType)) {
				// optional additional filter
				List<ConceptClass> classes = null;
				if (conceptClassResult != null) {
					classes = Arrays.asList(conceptClassResult) ;
				}
				List<Locale> locales = new ArrayList<Locale>(LocaleUtility.getLocalesInOrder());
				List<ConceptSearchResult> searchResults = conceptService.getConcepts(
					name, locales, context.getIncludeAll(), classes,
					null, null, null, null, context.getStartIndex(), context.getLimit()
				);
				List<Concept> concepts = new ArrayList<Concept>(searchResults.size());
				for (ConceptSearchResult csr : searchResults) {
					concepts.add(csr.getConcept());
				}
				return new NeedsPaging<Concept>(concepts, context);
			} else if (searchType == null || "equals".equals(searchType)) {
				Concept concept = conceptService.getConceptByName(name);
				// No concept by that name
				if (concept == null) {
					return new EmptySearchResult();
				}
				// Found the name, but if it doesn't have the same concept class ( if supplied ) it should be filtered out
				if (conceptClassResult != null && !concept.getConceptClass().equals(conceptClassResult)) {
					return new EmptySearchResult();
				}
				// Check concept name is fully specified
				boolean isPreferredOrFullySpecified = false;
				for (ConceptName conceptname : concept.getNames()) {
					if (conceptname.getName().equalsIgnoreCase(name)
							&& (conceptname.isPreferred() || conceptname.isFullySpecifiedName())) {
						isPreferredOrFullySpecified = true;
						break;
					}
				}
				if (!isPreferredOrFullySpecified) {
					throw new APIException("The concept name should be either a fully specified or locale preferred name");
				}
				List<Concept> concepts = Arrays.asList(concept);
				return new NeedsPaging<Concept>(concepts, context);
			} else {
				throw new InvalidSearchException("Invalid searchType: " + searchType
			        + ". Allowed values: \"equals\" and \"fuzzy\"");
			}
		} else if (conceptClass != null) { // searchType doesn't apply
			ConceptClass conceptClassResult = conceptService.getConceptClassByUuid(conceptClass);
			if (conceptClassResult == null) {
				return new EmptySearchResult();
			}
			List<Concept> concepts = conceptService.getConceptsByClass(conceptClassResult);
			return new NeedsPaging<Concept>(concepts, context);
		}
		
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
			List<Concept> concepts = new ArrayList<Concept>();
			List<ConceptMap> conceptMaps = conceptService.getConceptMappingsToSource(conceptSource);
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
