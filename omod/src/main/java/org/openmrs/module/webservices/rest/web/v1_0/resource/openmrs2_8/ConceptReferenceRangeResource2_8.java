/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_8;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptReferenceRange;
import org.openmrs.ConceptReferenceRangeContext;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_7.ConceptReferenceRangeResource2_7;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * {@link Resource} for {@link ConceptReferenceRange}, supporting standard CRUD operations
 */
@Resource(
		name = RestConstants.VERSION_1 + "/conceptreferencerange",
		supportedClass = ConceptReferenceRange.class,
		supportedOpenmrsVersions = "2.7.9 - 2.7.999, 2.8.5 - 9.*"
)
public class ConceptReferenceRangeResource2_8 extends ConceptReferenceRangeResource2_7 {

	@Override
	protected PageableResult doSearch(RequestContext context) {

		ConceptService conceptService = Context.getConceptService();
		List<ConceptReferenceRange> referenceRanges = new ArrayList<>();

		Obs obs = null;
		String obsUuid = context.getParameter("obs");
		if (StringUtils.isNotBlank(obsUuid)) {
			obs = Context.getObsService().getObsByUuid(obsUuid);
			if (obs == null) {
				throw new ObjectNotFoundException("No obs with uuid: " + obsUuid);
			}
		}

		Encounter encounter = null;
		String encounterUuid = context.getParameter("encounter");
		if (StringUtils.isNotBlank(encounterUuid)) {
			encounter = Context.getEncounterService().getEncounterByUuid(encounterUuid);
			if (encounter == null) {
				throw new ObjectNotFoundException("No encounter with uuid: " + encounterUuid);
			}
			if (obs != null && !obs.getEncounter().equals(encounter)) {
				throw new IllegalArgumentException("Obs encounter does not match provided encounter");
			}
		}

		Patient patient = null;
		String patientUuid = context.getParameter("patient");
		if (StringUtils.isNotBlank(patientUuid)) {
			patient = Context.getPatientService().getPatientByUuid(patientUuid);
			if (patient == null) {
				throw new ObjectNotFoundException("No patient with uuid: " + patientUuid);
			}
			if (obs != null && !obs.getPerson().equals(patient)) {
				throw new IllegalArgumentException("Obs person does not match provided patient");
			}
			if (encounter != null && !encounter.getPatient().equals(patient)) {
				throw new IllegalArgumentException("Encounter patient does not match provided patient");
			}
		}

		Date date = null;
		String dateParam = context.getParameter("date");
		if (StringUtils.isNotBlank(dateParam)) {
			date = (Date) ConversionUtil.convert(dateParam, Date.class);
		}

		String conceptRefs = context.getParameter("concept");
		if (StringUtils.isBlank(conceptRefs)) {
			throw new IllegalArgumentException("concept is required");
		}
		for (String conceptRef : conceptRefs.split(",")) {
			if (StringUtils.isNotBlank(conceptRef)) {
				Concept concept = conceptService.getConceptByReference(conceptRef.trim());
				if (concept == null) {
					throw new ObjectNotFoundException("No concept with ref: " + conceptRef);
				}
				ConceptReferenceRangeContext crrc = null;
				if (obs != null) {
					crrc = new ConceptReferenceRangeContext(obs);
				}
				else if (encounter != null) {
					crrc = new ConceptReferenceRangeContext(encounter, concept);
				}
				else if (patient != null) {
					crrc = new ConceptReferenceRangeContext(patient, concept, date);
				}
				else {
					throw new IllegalArgumentException("obs, encounter, or patient is required");
				}
				ConceptReferenceRange referenceRange = conceptService.getConceptReferenceRange(crrc);
				if (referenceRange != null) {
					referenceRanges.add(referenceRange);
				}
			}
		}
		return new NeedsPaging<>(referenceRanges, context);
	}
	
	@Override
	public String getResourceVersion() {
		return RestConstants2_8.RESOURCE_VERSION;
	}
}
