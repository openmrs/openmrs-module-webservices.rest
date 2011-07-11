package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.PersonAddress;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class PersonAddressResourceTest extends BaseDelegatingResourceTest<PersonAddress> {
	
	@Override
	public BaseDelegatingResource<PersonAddress> getResource() {
		return Context.getService(RestService.class).getResource(PersonAddressResource.class);
	}
	
	@Override
	public PersonAddress getObject() {
		return Context.getPersonService().getPersonByUuid(ResourceTestConstants.PERSON_UUID).getPersonAddress();
	}
	
}
