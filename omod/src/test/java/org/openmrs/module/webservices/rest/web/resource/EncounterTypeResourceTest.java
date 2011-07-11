package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class EncounterTypeResourceTest extends BaseDelegatingResourceTest<EncounterType> {
	
	@Override
	public BaseDelegatingResource<EncounterType> getResource() {
		return Context.getService(RestService.class).getResource(EncounterTypeResource.class);
	}
	
	@Override
	public EncounterType getObject() {
		return Context.getEncounterService().getEncounterTypeByUuid(ResourceTestConstants.ENCOUNTER_TYPE_UUID);
	}
	
}
