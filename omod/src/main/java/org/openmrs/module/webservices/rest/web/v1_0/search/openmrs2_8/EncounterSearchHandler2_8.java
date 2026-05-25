/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs2_8;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Visit;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EncounterSearchHandler2_8 implements SearchHandler {

    private static final String DATE_FROM = "fromdate";
    private static final String DATE_TO = "todate";
    private static final String PARAM_FORM = "form";
    private static final String PARAM_FORM_NAME = "formName";

    private final SearchConfig searchConfig = new SearchConfig("byForm", RestConstants.VERSION_1 + "/encounter",
            Collections.singletonList("2.8.* - 9.*"),
            Collections.singletonList(new SearchQuery.Builder(
                    "Allows you to find Encounters by patient, encounterType, form (by uuid), "
                            + "or formName (by name), and optionally by date range")
                    .withRequiredParameters("patient")
                    .withOptionalParameters("visit", "encounterType", DATE_FROM, DATE_TO,
                            "order", "totalCount", PARAM_FORM, PARAM_FORM_NAME)
                    .build()));

    @Override
    public SearchConfig getSearchConfig() {
        return this.searchConfig;
    }

    @Override
    
    public PageableResult search(RequestContext context) throws ResponseException {
        String patientUuid = context.getRequest().getParameter("patient");
        String encounterTypeUuid = context.getRequest().getParameter("encounterType");
        String[] visitUuids = context.getRequest().getParameterValues("visit");
        String formUuid = context.getRequest().getParameter(PARAM_FORM);
        String formName = context.getRequest().getParameter(PARAM_FORM_NAME);
        String dateFrom = context.getRequest().getParameter(DATE_FROM);
        String dateTo = context.getRequest().getParameter(DATE_TO);

        Date fromDate = dateFrom != null ? (Date) ConversionUtil.convert(dateFrom, Date.class) : null;
        Date toDate = dateTo != null ? (Date) ConversionUtil.convert(dateTo, Date.class) : null;

        Patient patient = ((PatientResource1_8) Context.getService(RestService.class)
                .getResourceBySupportedClass(Patient.class)).getByUniqueId(patientUuid);
        EncounterType encounterType = ((EncounterTypeResource1_8) Context.getService(RestService.class)
                .getResourceBySupportedClass(EncounterType.class)).getByUniqueId(encounterTypeUuid);

        if (patient == null || (encounterType == null && encounterTypeUuid != null)) {
            return new EmptySearchResult();
        }

        EncounterSearchCriteriaBuilder builder = new EncounterSearchCriteriaBuilder()
                .setPatient(patient)
                .setFromDate(fromDate)
                .setToDate(toDate)
                .setIncludeVoided(false);

        if (encounterType != null) {
            builder.setEncounterTypes(Arrays.asList(encounterType));
        }

        addVisits(builder, visitUuids);
        addForms(builder, formUuid, formName);

        List<Encounter> encounters = Context.getEncounterService()
                .getEncounters(builder.createEncounterSearchCriteria());

        if ("desc".equals(context.getRequest().getParameter("order"))) {
            Collections.reverse(encounters);
        }

        return new NeedsPaging<>(encounters, context);
    }

    private void addVisits(EncounterSearchCriteriaBuilder builder, String[] visitUuids) {
        if (visitUuids != null && visitUuids.length > 0) {
            List<Visit> visits = new ArrayList<>();
            for (String visitUuid : visitUuids) {
                visits.add(Context.getVisitService().getVisitByUuid(visitUuid));
            }
            builder.setVisits(visits);
        }
    }

    private void addForms(EncounterSearchCriteriaBuilder builder, String formUuid, String formName) {
        if (formUuid == null && formName == null) {
            return;
        }
        List<Form> forms = new ArrayList<>();
        if (formUuid != null) {
            Form form = Context.getFormService().getFormByUuid(formUuid);
            if (form != null) {
                forms.add(form);
            }
        } else {
            forms.addAll(Context.getFormService().getForms(formName, false, null, false, null, null, null));
        }
        if (!forms.isEmpty()) {
            builder.setEnteredViaForms(forms);
        }
    }
}