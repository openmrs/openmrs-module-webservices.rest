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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;

/**
 * This base search handler should be extended instead of directly implementing
 * {@link SearchHandler}.
 */
public abstract class BaseSearchHandler implements SearchHandler {
	
	private final Config config;
	
	protected BaseSearchHandler(Config config) {
		this.config = config;
	}
	
	protected static class ConfigBuilder {
		
		private final Config config = new Config();
		
		public ConfigBuilder() {
		}
		
		public ConfigBuilder setId(String id) {
			config.id = id;
			return this;
		}
		
		public ConfigBuilder setSupportedResource(String supportedResource) {
			config.supportedResource = supportedResource;
			return this;
		}
		
		public ConfigBuilder setSupportedOpenmrsVersions(String... supportedOpenmrsVersions) {
			config.supportedOpenmrsVersions = new HashSet<String>(Arrays.asList(supportedOpenmrsVersions));
			return this;
		}
		
		public ConfigBuilder setSearchQueries(SearchQuery... searchQueries) {
			config.searchQueries = new HashSet<SearchQuery>(Arrays.asList(searchQueries));
			return this;
		}
		
		public Config build() {
			Validate.notEmpty(config.id, "id must not be empty");
			Validate.notEmpty(config.supportedResource, "supportedResource must not be empty");
			Validate.notEmpty(config.supportedOpenmrsVersions, "supportedOpenmrsVersion must not be empty");
			Validate.notEmpty(config.searchQueries, "searchQueries must not be empty");
			return config;
		}
	}
	
	protected static class Config {
		
		private String id;
		
		private String supportedResource;
		
		private Set<String> supportedOpenmrsVersions;
		
		private Set<SearchQuery> searchQueries = Collections.emptySet();;
		
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getId()
	 */
	@Override
	public String getId() {
		return config.id;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSupportedResource()
	 */
	@Override
	public String getSupportedResource() {
		return config.supportedResource;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSupportedOpenmrsVersions()
	 */
	@Override
	public Set<String> getSupportedOpenmrsVersions() {
		return config.supportedOpenmrsVersions;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSearchQueries()
	 */
	@Override
	public Set<SearchQuery> getSearchQueries() {
		return config.searchQueries;
	}
}
