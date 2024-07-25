/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs2_4;

import com.google.common.base.Strings;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.EncounterTypeResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class EncounterSearchHandler2_4 implements SearchHandler {

    private static final String DATE_FROM = "fromdate";

    private static final String DATE_TO = "todate";
    private static final String ENCOUNTER_FORMS = "formUuid";
    private static final String OBS_CONCEPTS = "conceptUuid";

    private final SearchConfig searchConfig = new SearchConfig("byEncounterForms", RestConstants.VERSION_1 + "/encounter",
            Collections.singletonList("2.4.* - 9.*"),
            Collections.singletonList(new SearchQuery.Builder(
                    "Allows you to find Encounter by patient and encounterType (and optionally by encounter forms, obs concepts, from and to date range)")
                    .withRequiredParameters("patient").withOptionalParameters("encounterType", DATE_FROM, DATE_TO, ENCOUNTER_FORMS, OBS_CONCEPTS, "order")
                    .build()));

    @Override
    public SearchConfig getSearchConfig() {
        return this.searchConfig;
    }

    @Override
    public PageableResult search(RequestContext context) throws ResponseException {
        String patientUuid = context.getRequest().getParameter("patient");
        String encounterTypeUuid = context.getRequest().getParameter("encounterType");

        String dateFrom = context.getRequest().getParameter(DATE_FROM);
        String dateTo = context.getRequest().getParameter(DATE_TO);

        String forms = context.getRequest().getParameter(ENCOUNTER_FORMS);
        String concepts = context.getRequest().getParameter(OBS_CONCEPTS);

        Date fromDate = dateFrom != null ? (Date) ConversionUtil.convert(dateFrom, Date.class) : null;
        Date toDate = dateTo != null ? (Date) ConversionUtil.convert(dateTo, Date.class) : null;

        Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
                Patient.class)).getByUniqueId(patientUuid);
        EncounterType encounterType = ((EncounterTypeResource1_8) Context.getService(RestService.class)
                .getResourceBySupportedClass(EncounterType.class)).getByUniqueId(encounterTypeUuid);

        List<Form> formList = new ArrayList<>();
        if (!Strings.isNullOrEmpty(forms)) {
            FormService formService = Context.getFormService();
            Arrays.stream(forms.split(",")).map(formService::getFormByUuid).filter(Objects::nonNull).forEach(formList::add);
        }

        List<String> conceptUuidList = Strings.isNullOrEmpty(concepts) ? new ArrayList<>() : Arrays.asList(concepts.split(","));

        if (patient != null) {
            EncounterSearchCriteriaBuilder encounterSearchCriteriaBuilder = new EncounterSearchCriteriaBuilder()
                    .setPatient(patient).setFromDate(fromDate).setToDate(toDate).setIncludeVoided(false);
            if (encounterType != null) {
                encounterSearchCriteriaBuilder.setEncounterTypes(Collections.singletonList(encounterType));
            }
            encounterSearchCriteriaBuilder.setEnteredViaForms(formList);

            EncounterSearchCriteria encounterSearchCriteria = encounterSearchCriteriaBuilder.createEncounterSearchCriteria();

            List<Encounter> encounters = Context.getEncounterService().getEncounters(encounterSearchCriteria);
            encounters.forEach(encounter -> {
                if (!conceptUuidList.isEmpty()) {
                    List<Obs> obs = encounter.getObs().stream().filter(ob -> conceptUuidList.contains(ob.getConcept().getUuid())).collect(Collectors.toList());
                    encounter.setObs(new HashSet<>(obs));
                }
            });

            String order = context.getRequest().getParameter("order");
            if ("desc".equals(order)) {
                Collections.reverse(encounters);
            }
            return new NeedsPaging<Encounter>(encounters, context);
        }
        return new EmptySearchResult();
    }
}