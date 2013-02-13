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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.UserAndPassword;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.UserResource;

public class UserResourceTest extends BaseDelegatingResourceTest<UserResource, UserAndPassword> {
	
	@Override
	public UserAndPassword newObject() {
		UserAndPassword userAndPassword = new UserAndPassword(Context.getUserService().getUserByUuid(getUuidProperty()));
		userAndPassword.setPassword("topsecret");
		return userAndPassword;
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("username", getObject().getUser().getUsername());
		assertPropEquals("systemId", getObject().getUser().getSystemId());
		assertPropEquals("userProperties", getObject().getUser().getUserProperties());
		assertPropPresent("person");
		assertPropPresent("privileges");
		assertPropPresent("roles");
		assertPropEquals("retired", getObject().getUser().getRetired());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("username", getObject().getUser().getUsername());
		assertPropEquals("systemId", getObject().getUser().getSystemId());
		assertPropEquals("userProperties", getObject().getUser().getUserProperties());
		assertPropPresent("person");
		assertPropPresent("privileges");
		assertPropPresent("roles");
		assertPropPresent("allRoles");
		assertPropEquals("proficientLocales", getObject().getUser().getProficientLocales());
		assertPropEquals("secretQuestion", getObject().getUser().getSecretQuestion());
		assertPropEquals("retired", getObject().getUser().getRetired());
	}
	
	@Override
	public String getDisplayProperty() {
		return "butch - Hippocrates of Cos";
	}
	
	@Override
	public String getUuidProperty() {
		return ResourceTestConstants.USER_UUID;
	}
}
