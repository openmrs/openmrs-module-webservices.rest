/**
 * The contents of this file are subject to the OpenMRS Public LicenseV
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;

/**
 * Stores {@link SearchHandler}'s configuration.
 */
public class SearchConfig {
	
	private final String id;
	
	private final String supportedResource;
	
	private final Set<String> supportedOpenmrsVersions;
	
	private final Set<SearchQuery> searchQueries;
	
	public SearchConfig(String id, String supportedResource, Collection<String> supportedOpenmrsVersions,
	    Collection<SearchQuery> searchQueries) {
		this.id = id;
		this.supportedResource = supportedResource;
		this.supportedOpenmrsVersions = Collections.unmodifiableSet(new HashSet<String>(supportedOpenmrsVersions));
		this.searchQueries = Collections.unmodifiableSet(new HashSet<SearchQuery>(searchQueries));
		
		Validate.notEmpty(this.id, "id must not be empty");
		Validate.notEmpty(this.supportedResource, "supportedResource must not be empty");
		Validate.notEmpty(this.supportedOpenmrsVersions, "supportedOpenmrsVersions must not be empty");
		Validate.notEmpty(this.searchQueries, "searchQueries must not be empty");
	}
	
	public SearchConfig(String id, String supportedResource, String supportedOpenmrsVersion, SearchQuery searchQuery) {
		this(id, supportedResource, Arrays.asList(supportedOpenmrsVersion), Arrays.asList(searchQuery));
	}
	
	public SearchConfig(String id, String supportedResource, Collection<String> supportedOpenmrsVersions,
	    SearchQuery searchQuery) {
		this(id, supportedResource, supportedOpenmrsVersions, Arrays.asList(searchQuery));
	}
	
	public SearchConfig(String id, String supportedResource, String supportedOpenmrsVersion,
	    Collection<SearchQuery> searchQueries) {
		this(id, supportedResource, Arrays.asList(supportedOpenmrsVersion), searchQueries);
	}
	
	public String getId() {
		return id;
	}
	
	public String getSupportedResource() {
		return supportedResource;
	}
	
	public Set<String> getSupportedOpenmrsVersions() {
		return supportedOpenmrsVersions;
	}
	
	public Set<SearchQuery> getSearchQueries() {
		return searchQueries;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((supportedOpenmrsVersions == null) ? 0 : supportedOpenmrsVersions.hashCode());
		result = prime * result + ((supportedResource == null) ? 0 : supportedResource.hashCode());
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
		SearchConfig other = (SearchConfig) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (supportedOpenmrsVersions == null) {
			if (other.supportedOpenmrsVersions != null)
				return false;
		} else if (!supportedOpenmrsVersions.equals(other.supportedOpenmrsVersions))
			return false;
		if (supportedResource == null) {
			if (other.supportedResource != null)
				return false;
		} else if (!supportedResource.equals(other.supportedResource))
			return false;
		return true;
	}
}
