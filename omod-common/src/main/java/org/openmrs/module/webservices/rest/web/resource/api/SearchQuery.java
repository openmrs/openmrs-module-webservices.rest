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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;

/**
 * Used by {@link SearchConfig}.
 */
public class SearchQuery {
	
	private Set<String> requiredParameters;
	
	private Set<String> optionalParameters;
	
	private String description;
	
	private SearchQuery() {
	}
	
	public static class Builder {
		
		private SearchQuery searchQuery = new SearchQuery();
		
		public Builder(String description) {
			searchQuery.description = description;
		}
		
		public Builder withRequiredParameters(String... requiredParameters) {
			if (searchQuery.requiredParameters != null) {
				throw new IllegalStateException("requiredParameters() must not be called twice");
			}
			
			searchQuery.requiredParameters = Collections.unmodifiableSet(new HashSet<String>(Arrays
			        .asList(requiredParameters)));
			return this;
		}
		
		public Builder withOptionalParameters(String... optionalParameters) {
			if (searchQuery.optionalParameters != null) {
				throw new IllegalStateException("withOptionalParameters() must not be called twice");
			}
			
			searchQuery.optionalParameters = Collections.unmodifiableSet(new HashSet<String>(Arrays
			        .asList(optionalParameters)));
			return this;
		}
		
		public SearchQuery build() {
			if (searchQuery.requiredParameters == null) {
				searchQuery.requiredParameters = Collections.emptySet();
			}
			if (searchQuery.optionalParameters == null) {
				searchQuery.optionalParameters = Collections.emptySet();
			}
			
			Validate.notEmpty(searchQuery.description, "Description must not be empty");
			Validate.isTrue(!searchQuery.requiredParameters.isEmpty() || !searchQuery.optionalParameters.isEmpty(),
			    "Either required or optional parameters must not be empty");
			return searchQuery;
		}
	}
	
	public Set<String> getRequiredParameters() {
		return requiredParameters;
	}
	
	public Set<String> getOptionalParameters() {
		return optionalParameters;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((optionalParameters == null) ? 0 : optionalParameters.hashCode());
		result = prime * result + ((requiredParameters == null) ? 0 : requiredParameters.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchQuery other = (SearchQuery) obj;
		if (optionalParameters == null) {
			if (other.optionalParameters != null)
				return false;
		} else if (!optionalParameters.equals(other.optionalParameters))
			return false;
		if (requiredParameters == null) {
			if (other.requiredParameters != null)
				return false;
		} else if (!requiredParameters.equals(other.requiredParameters))
			return false;
		return true;
	}
	
}
