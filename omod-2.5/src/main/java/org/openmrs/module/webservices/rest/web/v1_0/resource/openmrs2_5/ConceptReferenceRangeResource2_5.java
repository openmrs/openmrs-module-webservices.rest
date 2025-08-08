/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_5;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for conceptreferencerange on platform versions before 2.7
 */
@Resource(name = RestConstants.VERSION_1 + "/conceptreferencerange", supportedClass = ConceptNumeric.class, supportedOpenmrsVersions = "2.5.* - 9.*", order = 1)
public class ConceptReferenceRangeResource2_5 extends DelegatingCrudResource<ConceptNumeric> {

	@Override
	public ConceptNumeric newDelegate() {
		return new ConceptNumeric();
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("concept");
			description.addProperty("hiNormal");
			description.addProperty("hiAbsolute");
			description.addProperty("hiCritical");
			description.addProperty("lowNormal");
			description.addProperty("lowAbsolute");
			description.addProperty("lowCritical");
			description.addProperty("units");
			description.addProperty("allowDecimal");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		}
		else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("concept");
			description.addProperty("hiNormal");
			description.addProperty("hiAbsolute");
			description.addProperty("hiCritical");
			description.addProperty("lowNormal");
			description.addProperty("lowAbsolute");
			description.addProperty("lowCritical");
			description.addProperty("units");
			description.addProperty("allowDecimal");
			description.addSelfLink();
			return description;
		}
		else if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		}
		
		return null;
	}
	
	@PropertyGetter("concept")
	public String getConcept(ConceptNumeric instance) {
		return instance.getUuid();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(ConceptNumeric instance) {
		String localization = getLocalization("Concept", instance.getUuid());
		if (StringUtils.isNotBlank(localization)) {
			return localization;
		} else {
			ConceptName cn = instance.getName();
			if (cn != null) {
				return cn.getName();
			} else {
				return instance.toString();
			}
		}
	}

	@Override
	public ConceptNumeric getByUniqueId(String uniqueId) {
		return Context.getConceptService().getConceptNumericByUuid(uniqueId);
	}

	@Override
	public ConceptNumeric save(ConceptNumeric delegate) {
		throw new UnsupportedOperationException("resource does not support this operation");
	}
	
	@Override
	protected void delete(ConceptNumeric delegate, String reason, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("resource does not support this operation");
	}

	@Override
	public void purge(ConceptNumeric delegate, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("resource does not support this operation");
	}

	@Override
	protected PageableResult doSearch(RequestContext context) {

		ConceptService conceptService = Context.getConceptService();
		List<ConceptNumeric> referenceRanges = new ArrayList<ConceptNumeric>();

		String conceptUuid = context.getParameter("concept");
		if (StringUtils.isBlank(conceptUuid)) {
			throw new IllegalArgumentException("concept is required");
		}
		
		String[] conceptReferenceStrings = conceptUuid.split(",");
		for (String conceptReference : conceptReferenceStrings) {
			if (StringUtils.isBlank(conceptReference)) {
 				continue;
 			}
 			// handle UUIDs
 			if (RestUtil.isValidUuid(conceptReference)) {
 				ConceptNumeric conceptNumeric = conceptService.getConceptNumericByUuid(conceptReference.trim());
 				if (conceptNumeric != null) {
 					referenceRanges.add(conceptNumeric);
 					continue;
 				}
 			}
 			// handle mappings
 			int idx = conceptReference.indexOf(':');
 			if (idx >= 0 && idx < conceptReference.length() - 1) {
 				String conceptSource = conceptReference.substring(0, idx);
 				String conceptCode = conceptReference.substring(idx + 1);
 				Concept concept = conceptService.getConceptByMapping(conceptCode.trim(), conceptSource.trim(), false);
 				if (concept != null && concept instanceof ConceptNumeric) {
 					referenceRanges.add((ConceptNumeric)concept);
 				}
 			}
		}

		return new NeedsPaging<ConceptNumeric>(referenceRanges, context);
	}
	
	@Override
	public String getResourceVersion() {
		return RestConstants2_5.RESOURCE_VERSION;
	}
}
