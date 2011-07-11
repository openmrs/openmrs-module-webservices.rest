package org.openmrs.module.webservices.rest.web.resource;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.hl7.HL7InQueue;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.util.IncomingHl7Message;

public class HL7MessageResourceTest extends BaseDelegatingResourceTest<IncomingHl7Message> {
	
	@Before
	public void before() throws Exception {
		executeDataSet(ResourceTestConstants.RESOURCE_TEST_DATASET);
	}
	
	@Override
	public BaseDelegatingResource<IncomingHl7Message> getResource() {
		return Context.getService(RestService.class).getResource(HL7MessageResource.class);
	}
	
	@Override
	public IncomingHl7Message getObject() {
		HL7InQueue msg = new HL7InQueue();
		msg.setHL7Data(ResourceTestConstants.HL7_SOURCE_NAME);
		msg.setHL7Source(Context.getHL7Service().getHL7SourceByName(ResourceTestConstants.HL7_SOURCE_NAME));
		Context.getHL7Service().saveHL7InQueue(msg);
		return new IncomingHl7Message(msg);
	}
	
}
