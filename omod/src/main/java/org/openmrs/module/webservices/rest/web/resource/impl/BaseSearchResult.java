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

import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.Hyperlink;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.SearchResult;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Base implementation that converts the result list to the requested representation, and adds next/prev links if necessary 
 * @param <T> the generic type of the list of results
 */
public abstract class BaseSearchResult<T> implements SearchResult {
	
	protected RequestContext context;
	
	public abstract List<T> getPageOfResults();
	
	public abstract boolean hasMoreResults();
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SearchResult#toSimpleObject()
	 */
	@Override
	public SimpleObject toSimpleObject() throws ResponseException {
		List<Object> results = new ArrayList<Object>();
		for (T match : getPageOfResults())
			results.add(ConversionUtil.convertToRepresentation(match, context.getRepresentation()));
		
		SimpleObject ret = new SimpleObject().add("results", results);
		boolean hasMore = hasMoreResults();
		if (context.getStartIndex() > 0 || hasMore) {
			List<Hyperlink> links = new ArrayList<Hyperlink>();
			if (hasMore)
				links.add(context.getNextLink());
			if (context.getStartIndex() > 0)
				links.add(context.getPreviousLink());
			ret.add("links", links);
		}
		return ret;
	}
	
}
