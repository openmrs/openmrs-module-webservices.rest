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
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_9;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmrs.ConceptMapType;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptReferenceTermMap;
import org.openmrs.api.ConceptService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.api.RestHelperService.Field;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Allows for searching {@link ConceptReferenceTermMap}s.
 */
@Component
public class ConceptReferenceTermMapSearchHandler1_9 implements SearchHandler {
	
	@Autowired
	@Qualifier("conceptService")
	ConceptService conceptService;
	
	@Autowired
	@Qualifier("restHelperService")
	RestHelperService restHelperService;
	
	private final SearchConfig searchConfig = new SearchConfig("default", RestConstants.VERSION_1
	        + "/conceptreferencetermmap", "1.9.*", Arrays.asList(
	    new SearchQuery.Builder("Allows you to find term maps by reference 'termA' (uuid) and 'maptype' (uuid or name)")
	            .withRequiredParameters("termA").withOptionalParameters("maptype").build(), new SearchQuery.Builder(
	            "Allows you to find term maps by reference 'termB' (uuid) and 'maptype' (uuid or name)")
	            .withRequiredParameters("termB").withOptionalParameters("maptype").build(), new SearchQuery.Builder(
	            "Allows you to find term maps by reference 'maps' (termA uuid) and 'to' (termB uuid)")
	            .withRequiredParameters("maps", "to").build(), new SearchQuery.Builder(
	            "Allows you to find term maps by reference 'maps' (termA or termB uuid) and 'maptype' (uuid or name)")
	            .withRequiredParameters("maps").withOptionalParameters("maptype").build()));
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#getSearchConfig()
	 */
	@Override
	public SearchConfig getSearchConfig() {
		return searchConfig;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SearchHandler#search(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public PageableResult search(RequestContext context) throws ResponseException {
		String termA = context.getParameter("termA");
		String termB = context.getParameter("termB");
		String maps = context.getParameter("maps");
		String to = context.getParameter("to");
		String maptype = context.getParameter("maptype");
		
		Field conceptMapTypeField = null;
		if (maptype != null) {
			ConceptMapType conceptMapType = conceptService.getConceptMapTypeByUuid(maptype);
			if (conceptMapType == null) {
				conceptMapType = conceptService.getConceptMapTypeByName(maptype);
			}
			
			if (conceptMapType == null) {
				return new EmptySearchResult();
			} else {
				conceptMapTypeField = new Field("conceptMapType", conceptMapType);
			}
		}
		
		Field toConceptReferenceTermField = null;
		if (to != null) {
			ConceptReferenceTerm toTerm = conceptService.getConceptReferenceTermByUuid(to);
			if (toTerm == null) {
				return new EmptySearchResult();
			} else {
				toConceptReferenceTermField = new Field("termB", toTerm);
			}
		}
		
		String searchTerm;
		if (termA != null) {
			searchTerm = termA;
		} else if (termB != null) {
			searchTerm = termB;
		} else {
			searchTerm = maps;
		}
		
		ConceptReferenceTerm term = conceptService.getConceptReferenceTermByUuid(searchTerm);
		if (term == null) {
			return new EmptySearchResult();
		}
		
		List<ConceptReferenceTermMap> termMaps = new ArrayList<ConceptReferenceTermMap>();
		if (termA != null) {
			termMaps.addAll(restHelperService.getObjectsByFields(ConceptReferenceTermMap.class, new Field("termA", term),
			    conceptMapTypeField));
		} else if (termB != null) {
			termMaps.addAll(restHelperService.getObjectsByFields(ConceptReferenceTermMap.class, new Field("termB", term),
			    conceptMapTypeField));
		} else if (to != null) {
			termMaps.addAll(restHelperService.getObjectsByFields(ConceptReferenceTermMap.class, new Field("termA", term),
			    toConceptReferenceTermField));
		} else {
			termMaps.addAll(restHelperService.getObjectsByFields(ConceptReferenceTermMap.class, new Field("termA", term),
			    conceptMapTypeField));
			termMaps.addAll(restHelperService.getObjectsByFields(ConceptReferenceTermMap.class, new Field("termB", term),
			    conceptMapTypeField));
		}
		
		return new NeedsPaging<ConceptReferenceTermMap>(termMaps, context);
	}
	
}
