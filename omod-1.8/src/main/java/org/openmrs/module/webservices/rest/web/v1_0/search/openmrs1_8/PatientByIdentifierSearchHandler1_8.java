package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_8;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PatientByIdentifierSearchHandler1_8 implements SearchHandler {

    private final SearchConfig searchConfig = new SearchConfig("patientByIdentifier", RestConstants.VERSION_1 + "/patient", Arrays.asList("1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"),
            Arrays.asList(new SearchQuery.Builder("Allows you to find Patients by identifier").withRequiredParameters("identifier").build()));

    @Override
    public SearchConfig getSearchConfig() {
        return this.searchConfig;
    }

    @Override
    public PageableResult search(RequestContext context) throws ResponseException {

        String identifier = context.getRequest().getParameter("identifier");

        if (StringUtils.isNotBlank(identifier)) {
            List<Patient> patients =  Context.getPatientService().getPatients(null, identifier, null, true);
            if (patients != null && patients.size() > 0) {
                return new NeedsPaging<Patient>(patients, context);
            }
        }

        return new EmptySearchResult();
    }
}
