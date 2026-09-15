/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.resource.api;

import java.util.List;

import org.openmrs.module.webservices.rest.TypedSimpleObject;
import org.openmrs.module.webservices.rest.web.Hyperlink;

/**
 * Data transfer object representing a paginated list of results.
 * 
 * @param <T> the underlying type of the elements in the result set
 */
public class PageableResultDto<T> {
	
	private List<TypedSimpleObject<T>> results;
	
	private Integer totalCount;
	
	private List<Hyperlink> links;
	
	public List<TypedSimpleObject<T>> getResults() {
		return results;
	}
	
	public void setResults(List<TypedSimpleObject<T>> results) {
		this.results = results;
	}
	
	public Integer getTotalCount() {
		return totalCount;
	}
	
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	
	public List<Hyperlink> getLinks() {
		return links;
	}
	
	public void setLinks(List<Hyperlink> links) {
		this.links = links;
	}
	
}
