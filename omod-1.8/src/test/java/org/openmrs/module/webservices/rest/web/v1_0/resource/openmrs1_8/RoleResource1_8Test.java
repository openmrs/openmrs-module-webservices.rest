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

import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.RoleResource1_8;

public class RoleResource1_8Test extends BaseDelegatingResourceTest<RoleResource1_8, Role> {
	
	@Override
	public Role newObject() {
		Role role = Context.getUserService().getRoleByUuid(getUuidProperty());
		return role;
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("privileges");
		assertPropPresent("inheritedRoles");
		assertPropEquals("retired", getObject().getRetired());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropPresent("privileges");
		assertPropPresent("inheritedRoles");
		assertPropPresent("allInheritedRoles");
		assertPropEquals("retired", getObject().getRetired());
	}
	
	@Override
	public String getDisplayProperty() {
		return "Provider";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_8.ROLE_UUID;
	}
}
