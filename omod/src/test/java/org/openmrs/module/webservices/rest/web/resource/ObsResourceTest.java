package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ObsResourceTest extends BaseDelegatingResourceTest<Obs> {
	
	@Override
	public BaseDelegatingResource<Obs> getResource() {
		return Context.getService(RestService.class).getResource(ObsResource.class);
	}
	
	@Override
	public Obs getObject() {
		return Context.getObsService().getObsByUuid(ResourceTestConstants.OBS_UUID);
	}
	
}
