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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;

/**
 * Search query used by {@link SearchHandler}.
 */
public class SearchQuery {
	
	private final Set<String> requiredParameters;
	
	private final Set<String> optionalParameters;
	
	private final String description;
	
	public SearchQuery(Collection<String> requiredParameters, Collection<String> optionalParameters, String description) {
		Validate.notEmpty(description, "Description must not be empty");
		
		if (requiredParameters != null) {
			this.requiredParameters = Collections.unmodifiableSet(new HashSet<String>(requiredParameters));
		} else {
			this.requiredParameters = Collections.emptySet();
		}
		
		if (optionalParameters != null) {
			this.optionalParameters = Collections.unmodifiableSet(new HashSet<String>(optionalParameters));
		} else {
			this.optionalParameters = Collections.emptySet();
		}
		
		Validate.isTrue(!this.requiredParameters.isEmpty() || !this.optionalParameters.isEmpty(),
		    "Either required or optional parameters must not be empty");
		
		this.description = description;
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
