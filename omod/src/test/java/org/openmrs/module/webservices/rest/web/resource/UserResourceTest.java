package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.UserAndPassword;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class UserResourceTest extends BaseDelegatingResourceTest<UserAndPassword> {
	
	@Override
	public BaseDelegatingResource<UserAndPassword> getResource() {
		return Context.getService(RestService.class).getResource(UserResource.class);
	}
	
	@Override
	public UserAndPassword getObject() {
		UserAndPassword userAndPassword = new UserAndPassword(Context.getAuthenticatedUser());
		userAndPassword.setPassword("topsecret");
		return userAndPassword;
	}
	
}
