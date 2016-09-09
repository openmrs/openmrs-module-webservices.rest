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
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_9;

import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Allows you to find terms by source and codeOrName or name.
 */
@Component
public class ConceptReferenceTermSearchHandler1_9 implements SearchHandler {

	@Autowired
	@Qualifier("conceptService")
	ConceptService conceptService;

	//default search type
	private static String SEARCH_TYPE_EQUALS = "equals";
	private static String SEARCH_TYPE_STARTS_WITH = "startsWith";

	private final SearchConfig searchConfig = new SearchConfig("default", RestConstants.VERSION_1 + "/conceptreferenceterm",
		Arrays.asList("1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*"), new SearchQuery.Builder("Allows you to find terms by source, codeOrName and name")
	                .withRequiredParameters("source").withOptionalParameters("codeOrName", "searchType").build());

	/**
	 * @see org.openmrs.module.webservices.rest.web.sresource.api.SearchHandler#getSearchConfig()
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
		String codeOrName = context.getParameter("codeOrName");
		String searchType = context.getParameter("searchType");

		if(searchType!=null&&!SEARCH_TYPE_EQUALS.equals(searchType)&&!SEARCH_TYPE_STARTS_WITH.equals(searchType)){
			throw new InvalidSearchException("Invalid searchType parameter");
		}

		if (searchType == null) {
			searchType = "equals";
		}

		if (searchType.equals(SEARCH_TYPE_EQUALS)) {
			ConceptSource conceptSource = conceptService.getConceptSourceByUuid(source);
			if (conceptSource == null) {
				conceptSource = conceptService.getConceptSourceByName(source);
				if (conceptSource == null) {
					return new EmptySearchResult();
				}
			}
			if (codeOrName != null) {
				return searchEquals(context, codeOrName, conceptSource);
			}
			else {
				List<ConceptReferenceTerm> terms = conceptService.getConceptReferenceTerms(null, conceptSource,
						context.getStartIndex(), context.getLimit(), context.getIncludeAll());
				int count = conceptService.getCountOfConceptReferenceTerms(null, conceptSource, context.getIncludeAll());
				boolean hasMore = count > (context.getStartIndex() + context.getLimit());
				return new AlreadyPaged<ConceptReferenceTerm>(context, terms, hasMore);
			}
		} else if (searchType.equals(SEARCH_TYPE_STARTS_WITH)) {
			List<ConceptSource> conceptSources = new ArrayList<ConceptSource>();
			ConceptSource singleConceptSource = conceptService.getConceptSourceByUuid(source);
			if (singleConceptSource != null) {
				conceptSources.add(singleConceptSource);
			}
			else {
				List<ConceptSource> allConceptSources = conceptService.getAllConceptSources();
				if (allConceptSources.isEmpty()) {
					return new EmptySearchResult();
				}
				else {
					for (ConceptSource conceptSource : allConceptSources) {
						if (conceptSource.getName().startsWith(source)) {
							conceptSources.add(conceptSource);
						}
					}
				}
			}
			return searchStartsWith(context, codeOrName, conceptSources);
		}
		else {
			throw new IllegalStateException("\"" + searchType + "\" doesn't fit searchType param requirements. Search type must be " + SEARCH_TYPE_EQUALS + " or " + SEARCH_TYPE_STARTS_WITH + ".");
		}
	}

	private PageableResult searchStartsWith(RequestContext context, String codeOrName, List<ConceptSource> conceptSources){
		/**
		 * org.openmrs.api.ConceptService#getConceptReferenceTerms(java.lang.String, org.openmrs.ConceptSource, java.lang.Integer, java.lang.Integer, boolean)
		 * returns all ConceptReferenceTerm objects which code or name match query, so results need to be filtered
		 */
		Set<ConceptReferenceTerm> resultTerms = new LinkedHashSet<ConceptReferenceTerm>();
		int count = 0;

		if (StringUtils.isNotEmpty(codeOrName)) {

			for (ConceptSource conceptSource : conceptSources) {
				resultTerms.addAll(conceptService.getConceptReferenceTerms(codeOrName, conceptSource, context.getStartIndex(), context.getLimit(), context.getIncludeAll()));
				count += conceptService.getCountOfConceptReferenceTerms(codeOrName, conceptSource, context.getIncludeAll());
			}
		}
		else {
			// Fetch all references by source
			for (ConceptSource conceptSource : conceptSources) {
				resultTerms.addAll(conceptService.getConceptReferenceTerms(null, conceptSource, context.getStartIndex(), context.getLimit(), context.getIncludeAll()));
				count += conceptService.getCountOfConceptReferenceTerms(null, conceptSource, context.getIncludeAll());
			}
		}

		if	(resultTerms.isEmpty()){
			return new EmptySearchResult();
		}
		else {
			boolean hasMore = count > (context.getStartIndex() + context.getLimit());

			List<ConceptReferenceTerm> listResult = new ArrayList<ConceptReferenceTerm>();
			listResult.addAll(resultTerms);

			return new AlreadyPaged<ConceptReferenceTerm>(context, listResult, hasMore);
		}
	}

	private PageableResult searchEquals(RequestContext context, String code, ConceptSource conceptSource) {
		ConceptReferenceTerm term = null;
		if (code != null) {
			term = conceptService.getConceptReferenceTermByCode(code, conceptSource);
		} else {
			return new EmptySearchResult();
		}

		if (term == null || (term.isRetired() && !context.getIncludeAll())) {
			return new EmptySearchResult();
		} else {
			return new AlreadyPaged<ConceptReferenceTerm>(context, Arrays.asList(term), false);
		}
	}

}
