/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs2_0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.api.OptionalParameter;
import org.openmrs.module.webservices.rest.web.resource.api.TypedSearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.TypedSearchHandler;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.EncounterTypeResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_2.EncounterResource2_2;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.springframework.stereotype.Component;

@Component
public class EncounterSearchHandler2_0 extends TypedSearchHandler<Encounter, EncounterResource2_2, EncounterSearchHandler2_0.Params> {

	static class Params {
		public String patient;

		@OptionalParameter
		public String[] visit;

		@OptionalParameter
		public String encounterType;

		@OptionalParameter
		public String fromdate;

		@OptionalParameter
		public String todate;

		/**
		 * if order is "desc", returns the result in reverse order
		 */
		@OptionalParameter
		public String order;
	}

	public EncounterSearchHandler2_0() {
		super(new TypedSearchConfig<>(
			"default", 
			EncounterResource2_2.class,
			Params.class, 
			"Allows you to find Encounter by patient and encounterType (and optionally by from and to date range)"
		));
	}
	
	@Override
	public List<Encounter> search(Params params) throws ResponseException {
		String patientUuid = params.patient;
		String encounterTypeUuid = params.encounterType;
		String[] visitUuids = params.visit;
		String dateFrom = params.fromdate;
		String dateTo = params.todate;
		String order = params.order;
		
		Date fromDate = dateFrom != null ? (Date) ConversionUtil.convert(dateFrom, Date.class) : null;
		Date toDate = dateTo != null ? (Date) ConversionUtil.convert(dateTo, Date.class) : null;
		
		Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
				Patient.class)).getByUniqueId(patientUuid);
		EncounterType encounterType = ((EncounterTypeResource1_8) Context.getService(RestService.class)
				.getResourceBySupportedClass(EncounterType.class)).getByUniqueId(encounterTypeUuid);

		if (patient != null && (encounterType != null || StringUtils.isBlank(encounterTypeUuid))) {
			EncounterSearchCriteriaBuilder encounterSearchCriteriaBuilder = new EncounterSearchCriteriaBuilder()
					.setPatient(patient).setFromDate(fromDate).setToDate(toDate).setIncludeVoided(false);
			if (encounterType != null) {
				encounterSearchCriteriaBuilder.setEncounterTypes(Arrays.asList(encounterType));
			}
			if (visitUuids != null && visitUuids.length > 0) {
				List<Visit> visits = new ArrayList<>();
				for (String visitUuid : visitUuids) {
					visits.add(Context.getVisitService().getVisitByUuid(visitUuid));
				}
				encounterSearchCriteriaBuilder.setVisits(visits);
			}

			EncounterSearchCriteria encounterSearchCriteria = encounterSearchCriteriaBuilder.createEncounterSearchCriteria();
			
			List<Encounter> encounters = Context.getEncounterService().getEncounters(encounterSearchCriteria);
			if ("desc".equals(order)) {
				Collections.reverse(encounters);
			}
			return encounters;
		}
		return Collections.emptyList();
	}
}
