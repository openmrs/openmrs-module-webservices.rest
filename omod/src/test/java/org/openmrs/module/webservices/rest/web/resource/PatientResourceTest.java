package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class PatientResourceTest extends BaseDelegatingResourceTest<Patient> {
	
	@Override
	public BaseDelegatingResource<Patient> getResource() {
		return Context.getService(RestService.class).getResource(PatientResource.class);
	}
	
	@Override
	public Patient getObject() {
		return Context.getService(PatientService.class).getPatientByUuid(ResourceTestConstants.PATIENT_UUID);
	}
	
}
