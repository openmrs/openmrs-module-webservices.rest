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
package org.openmrs.module.webservices.rest.web.resource.impl;

import java.util.List;

import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;

/**
 * Wraps a list of search results that has not yet had any possible paging settings from the request
 * context applied. Typically this will be used by implementations of {@link Searchable} and
 * {@link Listable} that do not have a native query capable of doing a page-limited search
 * 
 * @param <T> the generic type of the list of results
 */
public class NeedsPaging<T> extends BasePageableResult<T> {
	
	private List<T> unpagedResults;
	
	public NeedsPaging(List<T> unpagedResults, RequestContext context) {
		this.unpagedResults = unpagedResults;
		this.context = context;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BasePageableResult#getPageOfResults()
	 */
	@Override
	public List<T> getPageOfResults() {
		if (context.getStartIndex() == 0 && context.getLimit() >= unpagedResults.size()) {
			return unpagedResults;
		} else {
			int endIndex = context.getStartIndex() + context.getLimit();
			if (endIndex > unpagedResults.size())
				endIndex = unpagedResults.size();
			return unpagedResults.subList(context.getStartIndex(), endIndex);
		}
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BasePageableResult#hasMoreResults()
	 */
	@Override
	public boolean hasMoreResults() {
		return unpagedResults.size() > context.getStartIndex() + context.getLimit();
	}
	
}
