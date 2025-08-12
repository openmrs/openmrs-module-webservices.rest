/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_7;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptReferenceRange;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
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
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Resource} for {@link ConceptReferenceRange}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/conceptreferencerange", supportedClass = ConceptReferenceRange.class, supportedOpenmrsVersions = "2.7.* - 9.*", order = 2)
public class ConceptReferenceRangeResource2_7 extends DelegatingCrudResource<ConceptReferenceRange> {

	@Override
	public ConceptReferenceRange newDelegate() {
		return new ConceptReferenceRange();
	}

	@Override
	public ConceptReferenceRange save(ConceptReferenceRange delegate) {
		return Context.getConceptService().saveConceptReferenceRange(delegate);
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
	
	@PropertyGetter("units")
	public String getUnits(ConceptReferenceRange instance) {
		return instance.getConceptNumeric().getUnits();
	}
	
	@PropertyGetter("allowDecimal")
	public Boolean getAllowDecimal(ConceptReferenceRange instance) {
		return instance.getConceptNumeric().getAllowDecimal();
	}
	
	@PropertyGetter("concept")
	public String getConcept(ConceptReferenceRange instance) {
		return instance.getConceptNumeric().getUuid();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(ConceptReferenceRange instance) {
		String localization = getLocalization("Concept", instance.getConceptNumeric().getUuid());
		if (StringUtils.isNotBlank(localization)) {
			return localization;
		} else {
			ConceptName cn = instance.getConceptNumeric().getName();
			if (cn != null) {
				return cn.getName();
			} else {
				return instance.toString();
			}
		}
	}

	@Override
	public ConceptReferenceRange getByUniqueId(String uniqueId) {
		return Context.getConceptService().getConceptReferenceRangeByUuid(uniqueId);
	}

	@Override
	protected void delete(ConceptReferenceRange delegate, String reason, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("ConceptReferenceRange does not support this operation");
	}

	@Override
	public void purge(ConceptReferenceRange delegate, RequestContext context) throws ResponseException {
		Context.getConceptService().purgeConceptReferenceRange(delegate);
	}

	@Override
	protected PageableResult doSearch(RequestContext context) {

		ConceptService conceptService = Context.getConceptService();
		List<ConceptReferenceRange> referenceRanges = new ArrayList<ConceptReferenceRange>();
		
		String patientUuid = context.getParameter("patient");
		if (StringUtils.isBlank(patientUuid)) {
			throw new IllegalArgumentException("patient is required");
		}
		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		if (patient == null) {
			throw new ObjectNotFoundException("No patient with uuid: " + patientUuid);
		}

		String conceptUuid = context.getParameter("concept");
		if (StringUtils.isBlank(conceptUuid)) {
			throw new IllegalArgumentException("concept is required");
		}
		
		String[] conceptReferenceStrings = conceptUuid.split(",");
		for (String conceptReference : conceptReferenceStrings) {
			Concept concept = conceptService.getConceptByReference(conceptReference.trim());
			if (concept != null) {
				ConceptReferenceRange referenceRange = conceptService.getConceptReferenceRange(patient, concept);
				if (referenceRange != null) {
					referenceRanges.add(referenceRange);
				}
			}
		}

		return new NeedsPaging<ConceptReferenceRange>(referenceRanges, context);
	}
	
	@Override
	public String getResourceVersion() {
		return RestConstants2_7.RESOURCE_VERSION;
	}
}
