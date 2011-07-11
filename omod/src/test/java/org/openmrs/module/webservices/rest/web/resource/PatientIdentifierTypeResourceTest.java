package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.PatientIdentifier;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class PatientIdentifierTypeResourceTest extends BaseDelegatingResourceTest<PatientIdentifier> {
	
	@Override
	public BaseDelegatingResource<PatientIdentifier> getResource() {
		return Context.getService(RestService.class).getResource(PatientIdentifierResource.class);
	}
	
	@Override
	public PatientIdentifier getObject() {
		return Context.getService(PatientService.class).getPatientByUuid("da7f524f-27ce-4bb2-86d6-6d1d05312bd5")
		        .getPatientIdentifier();
	}
	
}
