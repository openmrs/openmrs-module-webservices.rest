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
package org.openmrs.module.webservices.rest.resource;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.WSConstants;

/**
 *
 */
@Handler(supports = { User.class })
public class UserResource<U extends User> implements OpenmrsResource<U> {

	// default
	protected String name;
	protected String gender;
	protected Date birthdate;
	protected String systemId;
	protected String username;
	protected boolean retired;
	protected String uuid;

	// optional
	
	protected AuditInfoResource<U> auditInfo;
	protected List<SimpleObject> names;
	protected List<SimpleObject> roles;
	protected Map<String, String> userProperties;
	

	public String getName(User u) {
		return u.getPersonName().getFullName();
	}

	public String getDisplay(U user) {
		return getName(user) + " (" + user.getUsername() + ")";
	}

	public String[] getDefaultRepresentation() {
		return new String[] { "name", "username" };
	}

	public String getURISuffix(U openmrsObject) {
		return WSConstants.URL_PREFIX + "/user/" + openmrsObject.getUuid();
	}

}
