package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class PersonResourceTest extends BaseDelegatingResourceTest<Person> {
	
	@Override
	public BaseDelegatingResource<Person> getResource() {
		return Context.getService(RestService.class).getResource(PersonResource.class);
	}
	
	@Override
	public Person getObject() {
		return Context.getPersonService().getPersonByUuid(ResourceTestConstants.PERSON_UUID);
	}
	
}
