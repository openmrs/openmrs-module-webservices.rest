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

import org.openmrs.ConceptReferenceTermMap;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ConceptReferenceTermMapResource1_9Test extends BaseDelegatingResourceTest<ConceptReferenceTermMapResource1_9, ConceptReferenceTermMap> {
	
	@Override
	public ConceptReferenceTermMap newObject() {
		return Context.getService(RestHelperService.class).getObjectByUuid(ConceptReferenceTermMap.class, getUuidProperty());
	}
	
	@Override
	public String getDisplayProperty() {
		return "Some Standardized Terminology: WGT234 - Some Standardized Terminology: CD41003";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_9.CONCEPT_REFERENCE_TERM_MAP_UUID;
	}
	
}
