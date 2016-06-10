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
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Allows you to find terms by source and code.
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
		Arrays.asList("1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*"), new SearchQuery.Builder("Allows you to find terms by source, code and name")
	                .withRequiredParameters("source").withOptionalParameters("code", "name", "searchType").build());

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
		String code = context.getParameter("code");
		String name = context.getParameter("name");
		String searchType = context.getParameter("searchType");

		if(searchType!=null&&!SEARCH_TYPE_EQUALS.equals(searchType)&&!SEARCH_TYPE_STARTS_WITH.equals(searchType)){
			throw new InvalidSearchException("Invalid searchType parameter");
		}

		ConceptSource conceptSource = conceptService.getConceptSourceByUuid(source);

		if (conceptSource == null) {
			conceptSource = conceptService.getConceptSourceByName(source);
		}

		if (conceptSource == null) {
			return new EmptySearchResult();
		}

		if (code != null || name != null) {
			if(SEARCH_TYPE_STARTS_WITH.equals(searchType)){
				return searchStartsWith(context, code, name, conceptSource);
			} else {
				return searchEquals(context, code, name, conceptSource);
			}
		} else {
			List<ConceptReferenceTerm> terms = conceptService.getConceptReferenceTerms(null, conceptSource,
					context.getStartIndex(), context.getLimit(), context.getIncludeAll());
			int count = conceptService.getCountOfConceptReferenceTerms(null, conceptSource, context.getIncludeAll());
			boolean hasMore = count > (context.getStartIndex() + context.getLimit());

			return new AlreadyPaged<ConceptReferenceTerm>(context, terms, hasMore);
		}
	}

	private PageableResult searchStartsWith(RequestContext context, String code, String name, ConceptSource conceptSource){
		/**
		 * org.openmrs.api.ConceptService#getConceptReferenceTerms(java.lang.String, org.openmrs.ConceptSource, java.lang.Integer, java.lang.Integer, boolean)
		 * returns all ConceptReferenceTerm objects which code or name match query, so results need to be filtered
		 */
		List<ConceptReferenceTerm> resultTerms = new ArrayList<ConceptReferenceTerm>();
		if(code != null){
			List<ConceptReferenceTerm> codeQueryTerms = conceptService.getConceptReferenceTerms(code, conceptSource, context.getStartIndex(),
					context.getLimit(), context.getIncludeAll());
			for(ConceptReferenceTerm term : codeQueryTerms){
				if(termMatchesCodeAndName(term, code, name)){
					resultTerms.add(term);
				}
			}
		}
		if(name != null){
			List<ConceptReferenceTerm> nameQueryTerms = conceptService.getConceptReferenceTerms(name, conceptSource, context.getStartIndex(),
					context.getLimit(), context.getIncludeAll());
			for(ConceptReferenceTerm term : nameQueryTerms){
				if(termMatchesCodeAndName(term, code, name)){
					resultTerms.add(term);
				}
			}
		}

		if(resultTerms.isEmpty()){
			return new EmptySearchResult();
		} else {
			return new AlreadyPaged<ConceptReferenceTerm>(context, resultTerms, false);
		}
	}

	private boolean termMatchesCodeAndName(ConceptReferenceTerm term, String code, String name){
		//all terms match unspecified code/name, to allow searching by name/code only
		boolean matchCode = true;
		boolean matchName = true;
		if(term == null) return false;
		if(code != null){
			matchCode = StringUtils.startsWithIgnoreCase(term.getCode(), code);
		}
		if(name != null){
			matchName = StringUtils.startsWithIgnoreCase(term.getName(), name);

		}
		return matchCode&&matchName;
	}

	private PageableResult searchEquals(RequestContext context, String code, String name, ConceptSource conceptSource) {
		ConceptReferenceTerm term = null;
		if (code != null) {
			term = conceptService.getConceptReferenceTermByCode(code, conceptSource);
			if (name != null && !name.equals(term.getName())) {
				term = null;
			}
		} else {
			term = conceptService.getConceptReferenceTermByName(name, conceptSource);
		}

		if (term == null || (term.isRetired() && !context.getIncludeAll())) {
			return new EmptySearchResult();
		} else {
			return new AlreadyPaged<ConceptReferenceTerm>(context, Arrays.asList(term), false);
		}
	}

}
