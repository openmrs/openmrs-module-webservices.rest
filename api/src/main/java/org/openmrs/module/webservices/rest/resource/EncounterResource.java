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

import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.module.webservices.rest.SimpleObject;

/**
 *
 */
@Handler(supports = { Encounter.class })
public class EncounterResource<E extends Encounter> implements
		OpenmrsResource<E> {

	// default
	protected Date encounterDatetime;
	protected SimpleObject location;
	protected SimpleObject patient;
	protected SimpleObject form;
	protected SimpleObject provider;
	protected SimpleObject encounterType;

	// optional

	protected AuditInfoResource<E> auditInfo;
	protected List<SimpleObject> obs;
	protected List<SimpleObject> orders;

	public String getDisplay(E enc) {
		return enc.getEncounterType().getName() + " " + enc.getLocation().getName() + " " + enc.getEncounterDatetime();
	}

	public String[] getDefaultRepresentation() {
		return new String[] { "encounterDatetime", "location", "patient" };
	}

	public String getURISuffix(E openmrsObject) {
		return "encounter/" + openmrsObject.getUuid();
	}

}
