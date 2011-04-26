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

import org.openmrs.PatientIdentifier;
import org.openmrs.annotation.Handler;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.WSConstants;

/**
 *
 */
@Handler(supports = { PatientIdentifier.class })
public class PatientIdentifierResource<PI extends PatientIdentifier> implements OpenmrsResource<PI> {
	
	// default
	protected String uuid;
	protected String identifier;
	protected SimpleObject identifierType;
	
	// optional
	protected Date deathDate;
	protected AuditInfoResource<PI> auditInfo;
	
    public String getDisplay(PI identifier) {
	    return identifier.getIdentifier() + " - " + identifier.getIdentifierType().getName();
    }
    
    public String[] getDefaultRepresentation() {
    	return new String[] { "identifier", "identifierType" };
    }

	public String getURISuffix(PI identifier) {
		return WSConstants.URL_PREFIX + "patient/" + identifier.getPatient().getUuid() + "/identifier/" + identifier.getUuid();
	}
		
}
