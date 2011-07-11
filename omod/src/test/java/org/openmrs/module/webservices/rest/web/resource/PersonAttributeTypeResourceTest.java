package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class PersonAttributeTypeResourceTest extends BaseDelegatingResourceTest<PersonAttributeType> {
	
	@Override
	public BaseDelegatingResource<PersonAttributeType> getResource() {
		return Context.getService(RestService.class).getResource(PersonAttributeTypeResource.class);
	}
	
	@Override
	public PersonAttributeType getObject() {
		return Context.getPersonService().getPersonAttributeTypeByUuid(ResourceTestConstants.PERSON_ATTRIBUTE_TYPE_UUID);
	}
}
