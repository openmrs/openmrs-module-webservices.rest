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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ConceptResource1_8;


/**
 * Implemented to increase the resource version since the mapping subresource changed.
 */
@Resource(name = RestConstants.VERSION_1 + "/concept", order = 1, supportedClass = Concept.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*"})
public class ConceptResource1_9 extends ConceptResource1_8 {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
	    return RestConstants1_9.RESOURCE_VERSION;
	}
	
	public Concept getByUniqueId(String identifier) {
		
		Concept concept = null;

		if (identifier.contains(":")) {
			String[] tokens = identifier.split(":");
			String sourceName = tokens[0];
			String termCode = tokens[1];
			concept = Context.getConceptService().getConceptByMapping(termCode, sourceName, true);
		} else {
			concept = Context.getConceptService().getConceptByUuid(identifier);
		}

		return concept;
	}
}
