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
 * Wraps a list of search results that has already had the paging settings from the RequestContext
 * applied. This should be used by implementations of {@link Searchable} that can natively query in
 * a page-limited way.
 * 
 * @param <T> the generic type of the list of results
 */
public class AlreadyPaged<T> extends BasePageableResult<T> {
	
	private List<T> results;
	
	private boolean hasMoreResults;
	
	public AlreadyPaged(RequestContext context, List<T> results, boolean hasMoreResults) {
		this.context = context;
		this.results = results;
		this.hasMoreResults = hasMoreResults;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BasePageableResult#getPageOfResults()
	 */
	@Override
	public List<T> getPageOfResults() {
		return results;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BasePageableResult#hasMoreResults()
	 */
	@Override
	public boolean hasMoreResults() {
		return hasMoreResults;
	}
	
}
