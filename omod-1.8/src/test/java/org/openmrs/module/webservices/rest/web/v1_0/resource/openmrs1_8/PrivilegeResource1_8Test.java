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

import java.util.UUID;

import org.openmrs.Privilege;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PrivilegeResource1_8;

public class PrivilegeResource1_8Test extends BaseDelegatingResourceTest<PrivilegeResource1_8, Privilege> {
	
	private String uuid;
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#newObject()
	 */
	@Override
	public Privilege newObject() {
		Privilege privilege = new Privilege("PrivilegeResourceTest Privilege", "This privilege is only for testing.");
		privilege.setUuid(UUID.randomUUID().toString()); //Uuid isn't assigned during creation until 1.8.1. 
		uuid = privilege.getUuid();
		Context.getUserService().savePrivilege(privilege);
		return privilege;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#getDisplayProperty()
	 */
	@Override
	public String getDisplayProperty() {
		return "PrivilegeResourceTest Privilege";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest#getUuidProperty()
	 */
	@Override
	public String getUuidProperty() {
		return uuid;
	}
}
