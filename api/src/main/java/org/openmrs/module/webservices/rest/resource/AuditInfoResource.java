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

import org.openmrs.Auditable;
import org.openmrs.module.webservices.rest.SimpleObject;

/**
 *
 */
//@Handler(supports = { Auditable.class })
public class AuditInfoResource<A extends Auditable> implements OpenmrsResource<A> {
	
	protected SimpleObject creator;
	protected Date dateCreated;
	
	protected SimpleObject changedBy;
	protected Date dateChanged;
	
	protected SimpleObject voidedBy;
	protected Date dateVoided;
	protected String voidReason;
	
	protected SimpleObject retiredBy;
	protected Date dateRetired;
	protected String retireReason;
	
    public String getDisplay(A openmrsObject) {
    	// this method shouldn't be used
    	return "";
    }
    
    public String getURISuffix(A openmrsObject) {
    	// this method shouldn't be used
		return "";
	}
    
    public String[] getDefaultRepresentation() {
    	return new String[] { "creator", "dateCreated", "changedBy", "dateChanged" };
    }

	
		
}
