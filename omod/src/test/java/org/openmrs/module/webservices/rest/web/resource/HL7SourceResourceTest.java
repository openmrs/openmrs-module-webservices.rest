package org.openmrs.module.webservices.rest.web.resource;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.hl7.HL7Source;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class HL7SourceResourceTest extends BaseDelegatingResourceTest<HL7Source> {
	
	@Before
	public void before() throws Exception {
		executeDataSet(ResourceTestConstants.RESOURCE_TEST_DATASET);
	}
	
	@Override
	public BaseDelegatingResource<HL7Source> getResource() {
		return Context.getService(RestService.class).getResource(HL7SourceResource.class);
	}
	
	@Override
	public HL7Source getObject() {
		return Context.getHL7Service().getHL7SourceByName(ResourceTestConstants.HL7_SOURCE_NAME);
	}
	
}
