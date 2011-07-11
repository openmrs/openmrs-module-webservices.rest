package org.openmrs.module.webservices.rest.web.resource;

import org.junit.Before;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class CohortResourceTest extends BaseDelegatingResourceTest<Cohort> {
	
	@Before
	public void before() throws Exception {
		executeDataSet(ResourceTestConstants.RESOURCE_TEST_DATASET);
	}
	
	@Override
	public BaseDelegatingResource<Cohort> getResource() {
		return Context.getService(RestService.class).getResource(CohortResource.class);
	}
	
	@Override
	public Cohort getObject() {
		return Context.getCohortService().getCohortByUuid(ResourceTestConstants.COHORT_UUID);
	}
	
}
