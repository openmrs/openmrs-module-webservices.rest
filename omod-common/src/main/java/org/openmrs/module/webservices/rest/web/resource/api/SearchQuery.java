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
package org.openmrs.module.webservices.rest.web.resource.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;

/**
 * Used by {@link SearchHandlerConfig}.
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
			searchQuery.requiredParameters = Collections.unmodifiableSet(new HashSet<String>(Arrays
			        .asList(requiredParameters)));
			return this;
		}
		
		public Builder withOptionalParameters(String... optionalParameters) {
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
