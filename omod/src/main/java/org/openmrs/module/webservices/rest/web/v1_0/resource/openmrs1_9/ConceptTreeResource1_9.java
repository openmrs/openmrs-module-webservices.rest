/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptSet;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Resource(name = RestConstants.VERSION_1 + "/concepttree", supportedClass = SimpleObject.class, supportedOpenmrsVersions = {
		"1.9.* - 9.*" })
public class ConceptTreeResource1_9 extends BaseDelegatingResource<SimpleObject> implements Searchable {

	public static final String REQUEST_PARAM_CONCEPT = "concept";

	@Override
	public SimpleObject newDelegate() {
		throw new ResourceDoesNotSupportOperationException("concepttree doesn't support this action");
	}

	@Override
	public SimpleObject save(SimpleObject delegate) {
		throw new ResourceDoesNotSupportOperationException("concepttree doesn't support this action");
	}

	@Override
	public SimpleObject getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException("concepttree doesn't support this action");
	}

	@Override
	protected void delete(SimpleObject delegate, String reason, RequestContext context) {
		throw new ResourceDoesNotSupportOperationException("concepttree doesn't support this action");
	}

	@Override
	public void purge(SimpleObject delegate, RequestContext context) {
		throw new ResourceDoesNotSupportOperationException("concepttree doesn't support this action");
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		return new DelegatingResourceDescription();
	}

	@Override
	public SimpleObject search(RequestContext context) throws ResponseException {
		String conceptReferenceParam = context.getRequest().getParameter(REQUEST_PARAM_CONCEPT);
		if (StringUtils.isBlank(conceptReferenceParam)) {
			throw new InvalidSearchException("The parameter " + REQUEST_PARAM_CONCEPT + " is required");
		}

		Concept concept = Context.getConceptService().getConceptByReference(conceptReferenceParam);
		if (concept == null) {
			throw new ObjectNotFoundException("No concept found: " + conceptReferenceParam);
		}

		return buildConceptTree(concept, new HashSet<String>());
	}

	private SimpleObject buildConceptTree(Concept concept, Set<String> visitedUuids) {
		SimpleObject map = new SimpleObject();
		map.put("uuid", concept.getUuid());
		map.put("display", concept.getDisplayString());
		map.put("isSet", concept.getSet());

		if (visitedUuids.contains(concept.getUuid())) {
			map.put("setMembers", Collections.emptyList());
			return map;
		}

		visitedUuids.add(concept.getUuid());

		if (concept.getSet()) {
			List<SimpleObject> childrenList = new ArrayList<SimpleObject>();
			ConceptService conceptService = Context.getConceptService();
			List<ConceptSet> conceptSets = conceptService.getConceptSetsByConcept(concept);
			for (ConceptSet set : conceptSets) {
				childrenList.add(buildConceptTree(set.getConcept(), new HashSet<>(visitedUuids)));
			}
			map.put("setMembers", childrenList);
		} else {
			map.put("setMembers", new ArrayList<SimpleObject>());
		}

		return map;
	}
}
