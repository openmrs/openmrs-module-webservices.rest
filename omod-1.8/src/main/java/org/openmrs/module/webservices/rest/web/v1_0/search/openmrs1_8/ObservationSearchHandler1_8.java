package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_8;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
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
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ConceptResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

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
@Component
public class ObservationSearchHandler1_8 implements SearchHandler {

    private final SearchConfig searchConfig = new SearchConfig("default", RestConstants.VERSION_1 + "/obs", Arrays.asList("1.8.*", "1.9.*","1.10.*"),
            Arrays.asList(new SearchQuery.Builder("Allows you to find Observations by patient and concept").withRequiredParameters("patient", "concept").build()));

    @Override
    public SearchConfig getSearchConfig() {
        return this.searchConfig;
    }

    @Override
    public PageableResult search(RequestContext context) throws ResponseException {

        String patientUuid = context.getRequest().getParameter("patient");
        String conceptUuid = context.getRequest().getParameter("concept");
        List<Concept> concepts = null;

        if (patientUuid != null) {
            Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
                    Patient.class)).getByUniqueId(patientUuid);
            if (patient != null && conceptUuid != null){
                if (conceptUuid != null) {
                    Concept concept = ((ConceptResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
                            Concept.class)).getByUniqueId(conceptUuid);

                    List<Obs> obs = Context.getObsService().getObservationsByPersonAndConcept(patient,concept);
                    return new NeedsPaging<Obs>(obs, context);
                }

            }
        }

        return new EmptySearchResult();
    }
}


