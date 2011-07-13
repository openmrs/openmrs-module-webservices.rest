/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.UserAndPassword;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class UserResourceTest extends BaseDelegatingResourceTest<UserResource, UserAndPassword> {
	
	@Override
	public UserAndPassword newObject() {
		UserAndPassword userAndPassword = new UserAndPassword(Context.getAuthenticatedUser());
		userAndPassword.setPassword("topsecret");
		return userAndPassword;
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUser().getUuid());
		assertEquals("retired", getObject().getUser().getRetired());
		assertEquals("display", getResource().getDisplayString(getObject()));
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUser().getUuid());
		assertEquals("username", getObject().getUser().getUsername());
		assertEquals("systemId", getObject().getUser().getSystemId());
		assertEquals("userProperties", getObject().getUser().getUserProperties());
		assertContains("person");
		assertContains("roles");
		assertEquals("retired", getObject().getUser().getRetired());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUser().getUuid());
		assertEquals("username", getObject().getUser().getUsername());
		assertEquals("systemId", getObject().getUser().getSystemId());
		assertEquals("userProperties", getObject().getUser().getUserProperties());
		assertContains("person");
		assertContains("roles");
		assertEquals("proficientLocales", getObject().getUser().getProficientLocales());
		assertEquals("secretQuestion", getObject().getUser().getSecretQuestion());
		assertEquals("retired", getObject().getUser().getRetired());
	}
}
