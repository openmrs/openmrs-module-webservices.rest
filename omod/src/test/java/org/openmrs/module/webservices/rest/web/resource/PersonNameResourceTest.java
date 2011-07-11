package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class PersonNameResourceTest extends BaseDelegatingResourceTest<PersonName> {
	
	@Override
	public BaseDelegatingResource<PersonName> getResource() {
		return Context.getService(RestService.class).getResource(PersonNameResource.class);
	}
	
	@Override
	public PersonName getObject() {
		return Context.getPersonService().getPersonByUuid(ResourceTestConstants.PERSON_UUID).getPersonName();
	}
	
}
