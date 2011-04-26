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

import org.openmrs.OpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.WSConstants;

/**
 *
 */
@Handler(supports = { Patient.class })
public class PatientResource<P extends Patient> implements OpenmrsResource<P> {
	
	// default
	protected String name;
	protected String gender;
	protected Date birthdate;
	protected boolean dead;
	protected boolean voided;
	protected String uuid;
	protected String identifier;
	protected SimpleObject identifierType;
	
	// optional
	protected Date deathDate;
	protected AuditInfoResource<P> auditInfo;
	protected List<SimpleObject> identifiers;
	protected List<SimpleObject> names;
	protected List<SimpleObject> addresses;
	protected List<SimpleObject> attributes;
	
	public String getName(P person) {
		return person.getPersonName().getFullName();
	}
	
	public String getIdentifier(P patient) {
		return patient.getPatientIdentifier().getIdentifier();
	}
	
	public OpenmrsObject getIdentifierType(P patient) {
		return patient.getPatientIdentifier().getIdentifierType();
	}

    public String getDisplay(P person) {
	    return getName(person);
    }
    
    public String[] getDefaultRepresentation() {
    	return new String[] { "name", "uuid", "birthdate", "gender", "creator", "names" };
    }

	public String getURISuffix(P openmrsObject) {
		return WSConstants.URL_PREFIX + "patient/" + openmrsObject.getUuid();
	}
		
}
