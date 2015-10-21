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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.LocaleUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Find drugs that match the specified search phrase. The logic matches on drug names, concept names
 * of the associated concepts or the concept reference term codes of the drug reference term
 * mappings.
 */
@Component
public class DrugSearchHandler1_10 implements SearchHandler {
	
	public static final String REQUEST_PARAM_QUERY = "q";
	
	public static final String REQUEST_PARAM_LOCALE = "locale";
	
	public static final String REQUEST_PARAM_EXACT_LOCALE = "exactLocale";
	
	@Autowired
	@Qualifier("conceptService")
	ConceptService conceptService;
	
	SearchQuery searchQuery = new SearchQuery.Builder("Allows you to search for drugs, it matches on drug"
	        + " names, concept names of the associated concepts or the concept reference term codes of the"
	        + " drug reference term mappings").withRequiredParameters(REQUEST_PARAM_QUERY)
	        .withOptionalParameters(REQUEST_PARAM_LOCALE, REQUEST_PARAM_EXACT_LOCALE).build();
	
	private final SearchConfig searchConfig = new SearchConfig("default", RestConstants.VERSION_1 + "/drug",
	        Arrays.asList("1.10.*", "1.11.*", "1.12.*", "2.0.*"), searchQuery);
	
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
		String query = context.getParameter(REQUEST_PARAM_QUERY);
		String localeString = context.getParameter(REQUEST_PARAM_LOCALE);
		String exactLocaleString = context.getParameter(REQUEST_PARAM_EXACT_LOCALE);
		Locale locale = null;
		boolean exactLocale = false;
		if (StringUtils.isNotBlank(localeString)) {
			locale = LocaleUtility.fromSpecification(localeString);
			if (locale == null) {
				throw new InvalidSearchException("Unknown locale:" + localeString);
			}
		}
		
		if (StringUtils.isNotBlank(exactLocaleString)) {
			exactLocale = Boolean.valueOf(exactLocaleString);
		}
		
		List<Drug> drugs = conceptService.getDrugs(query, locale, exactLocale, context.getIncludeAll());
		return new NeedsPaging<Drug>(drugs, context);
	}
}
