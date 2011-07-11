package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class LocationResourceTest extends BaseDelegatingResourceTest<Location> {
	
	@Override
	public BaseDelegatingResource<Location> getResource() {
		return Context.getService(RestService.class).getResource(LocationResource.class);
	}
	
	@Override
	public Location getObject() {
		return Context.getLocationService().getLocationByUuid(ResourceTestConstants.LOCATION_UUID);
	}
	
}
