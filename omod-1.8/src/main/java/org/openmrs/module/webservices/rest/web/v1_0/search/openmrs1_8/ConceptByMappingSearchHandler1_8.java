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
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_8;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

/**
 * Allows for finding concepts by mapping.
 */
@Component
public class ConceptByMappingSearchHandler1_8 implements SearchHandler {
	
	private Set<String> supportedOpenmrsVersions = new HashSet<String>(Arrays.asList("1.8.*", "1.9.*"));
	
	private Set<String> requiredSearchParameters = new HashSet<String>(Arrays.asList("sourceName", "code"));
	
	private Set<String> optionalSearchParameters = Collections.emptySet();
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getOrder()
	 */
	@Override
	public String getId() {
		return "conceptsByMapping";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSupportedResource()
	 */
	@Override
	public String getSupportedResource() {
		return "concept";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSupportedOpenmrsVersions()
	 */
	@Override
	public Set<String> getSupportedOpenmrsVersions() {
		return supportedOpenmrsVersions;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSearchDescription()
	 */
	@Override
	public String getSearchDescription() {
		return "Allows you to find concepts by sourceName and code";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getRequiredParameters()
	 */
	@Override
	public Set<String> getRequiredParameters() {
		return requiredSearchParameters;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getOptionalParameters()
	 */
	@Override
	public Set<String> getOptionalParameters() {
		return optionalSearchParameters;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#search(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public PageableResult search(RequestContext context) throws ResponseException {
		String sourceName = context.getParameter("sourceName");
		String code = context.getParameter("code");
		List<Concept> conceptsByMapping = Context.getConceptService().getConceptsByMapping(code, sourceName);
		return new NeedsPaging<Concept>(conceptsByMapping, context);
	}
	
}
