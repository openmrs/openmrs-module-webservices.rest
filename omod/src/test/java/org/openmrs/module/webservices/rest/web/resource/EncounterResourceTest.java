package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class EncounterResourceTest extends BaseDelegatingResourceTest<Encounter> {
	
	@Override
	public BaseDelegatingResource<Encounter> getResource() {
		return Context.getService(RestService.class).getResource(EncounterResource.class);
	}
	
	@Override
	public Encounter getObject() {
		return Context.getEncounterService().getEncounterByUuid(ResourceTestConstants.ENCOUNTER_UUID);
	}
	
}
