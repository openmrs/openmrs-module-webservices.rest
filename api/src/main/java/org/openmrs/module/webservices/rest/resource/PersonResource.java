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

import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.WSConstants;
import org.openmrs.module.webservices.rest.annotation.WSCascade;
import org.openmrs.module.webservices.rest.annotation.WSProperty;

/**
 *
 */
@Handler(supports = { Person.class })
public class PersonResource<P extends Person> implements OpenmrsResource<P> {
	
	// default
	@WSProperty({"default", "required"})
	protected String name;
	
	@WSProperty({"default", "required"})
	protected String gender;
	
	@WSProperty({"default", "required"})
	protected Date birthdate;
	
	@WSProperty("default")
	protected boolean dead;
	
	@WSProperty("default")
	protected boolean voided;
	
	@WSProperty("default")
	protected String uuid;
	
	// optional
	@WSProperty("medium")
	protected Date deathDate;
	
	@WSProperty("medium")
	@WSCascade(cascadeFullAs="partial")
	protected List<SimpleObject> names;
	
	protected List<SimpleObject> addresses;
	
	@WSCascade(cascadeFullAs="partial")
	protected List<SimpleObject> attributes;
	
	protected AuditInfoResource<P> auditInfo;
	
	public String getName(P person) {
		return person.getPersonName().getFullName();
	}

    public String getDisplay(P person) {
	    return getName(person);
    }
    
    public String[] getDefaultRepresentation() {
    	return new String[] { "name", "uuid", "birthdate", "gender", "creator", "names" };
    }

	public String getURISuffix(P openmrsObject) {
		return WSConstants.URL_PREFIX + "person/" + openmrsObject.getUuid();
	}
		
}
