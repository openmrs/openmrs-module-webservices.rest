package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.PatientIdentifierType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * This is a placeholder implementation
 */
@Resource("patientidentifiertype")
@Handler(supports=PatientIdentifierType.class, order=0)
public class PatientIdentifierTypeResource extends MetadataDelegatingCrudResource<PatientIdentifierType> {

	private PatientService service() {
		return Context.getPatientService();
	}
	
	@Override
    public PatientIdentifierType getByUniqueId(String uniqueId) {
	    return service().getPatientIdentifierTypeByUuid(uniqueId);
    }

	@Override
    protected PatientIdentifierType newDelegate() {
	   return new PatientIdentifierType();
    }

	@Override
    protected PatientIdentifierType save(PatientIdentifierType delegate) {
	    return service().savePatientIdentifierType(delegate);
    }

	@Override
    public void purge(PatientIdentifierType delegate, RequestContext context) throws ResponseException {
	    service().purgePatientIdentifierType(delegate);
    }

	@Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
	    return null;
    }

}
