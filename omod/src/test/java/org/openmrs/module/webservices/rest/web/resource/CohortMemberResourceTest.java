package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.CohortMember;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class CohortMemberResourceTest extends BaseDelegatingResourceTest<CohortMember> {
	
	@Override
	public BaseDelegatingResource<CohortMember> getResource() {
		return Context.getService(RestService.class).getResource(CohortMemberResource.class);
	}
	
	@Override
	public CohortMember getObject() {
		Cohort cohort = Context.getCohortService().getCohortByUuid(ResourceTestConstants.COHORT_UUID);
		Patient patient = Context.getPatientService().getPatientByUuid(ResourceTestConstants.PATIENT_UUID);
		return new CohortMember(patient, cohort);
	}
	
}
