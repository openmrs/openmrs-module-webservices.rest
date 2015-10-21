/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p/>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p/>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_8;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
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
import org.springframework.stereotype.Component;

@Component
public class EncounterSearchHandler1_8 implements SearchHandler {
	
	private static final String DATE_FROM = "fromdate";
	private static final String DATE_TO = "todate";
	
    private final SearchConfig searchConfig = new SearchConfig("default", RestConstants.VERSION_1 + "/encounter", Arrays.asList("1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*"),
            Arrays.asList(new SearchQuery.Builder("Allows you to find Encounter by patient and encounterType (and optionally by from and to date range)").withRequiredParameters("patient", "encounterType").withOptionalParameters(DATE_FROM, DATE_TO, "order").build()));

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
        
        Date fromDate = dateFrom != null ? (Date)ConversionUtil.convert(dateFrom, Date.class) : null;
        Date toDate = dateTo != null? (Date)ConversionUtil.convert(dateTo, Date.class) : null;
        
        Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
                Patient.class)).getByUniqueId(patientUuid);
        EncounterType encounterType = ((EncounterTypeResource1_8)
                Context.getService(RestService.class).getResourceBySupportedClass(EncounterType.class)).getByUniqueId(encounterTypeUuid);
        if (patient != null && encounterType != null) {
            List<Encounter> encounters = Context.getEncounterService()
                    .getEncounters(patient, null, fromDate, toDate, null, Arrays.asList(encounterType), null, false);
            String order = context.getRequest().getParameter("order");
            if ("desc".equals(order)) {
            	Collections.reverse(encounters);
            }
            return new NeedsPaging<Encounter>(encounters, context);
        }
        return new EmptySearchResult();
    }

}

