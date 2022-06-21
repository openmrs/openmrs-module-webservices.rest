/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_2;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_1.ConceptSourceResource2_1;

public class ConceptResource2_2 extends ConceptSourceResource2_1 {

	@Override
	public String getResourceVersion() {
		return RestConstants2_2.RESOURCE_VERSION;
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
