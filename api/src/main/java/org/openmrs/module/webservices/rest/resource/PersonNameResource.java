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

import org.openmrs.PersonName;
import org.openmrs.annotation.Handler;

/**
 *
 */
@Handler(supports = { PersonName.class })
public class PersonNameResource<PN extends PersonName> implements
		OpenmrsResource<PN> {

	protected boolean preferred;
	protected String prefix;
	protected String givenName;
	protected String middleName;
	protected String familyNamePrefix;
	protected String familyName;
	protected String familyName2;
	protected String familyNameSuffix;
	protected String degree;
	protected String uuid;

	public String getDisplay(PN pn) {
		return pn.getFullName();
	}

	public String[] getDefaultRepresentation() {
		return new String[] { "givenName", "familyName" };
	}

	public String getURISuffix(PN name) {
		return "person/" + name.getPerson().getUuid() + "/name/" + name.getUuid();
	}
}
