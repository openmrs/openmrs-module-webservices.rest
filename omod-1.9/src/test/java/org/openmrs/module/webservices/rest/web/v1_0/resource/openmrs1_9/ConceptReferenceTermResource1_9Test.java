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

import org.openmrs.ConceptReferenceTerm;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ConceptReferenceTermResource1_9Test extends BaseDelegatingResourceTest<ConceptReferenceTermResource1_9, ConceptReferenceTerm> {
	
	@Override
	public ConceptReferenceTerm newObject() {
		return Context.getConceptService().getConceptReferenceTermByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("code", getObject().getCode());
		assertPropEquals("version", getObject().getVersion());
		assertPropEquals("retired", getObject().isRetired());
		assertPropPresent("conceptSource");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("code", getObject().getCode());
		assertPropEquals("version", getObject().getVersion());
		assertPropEquals("retired", getObject().isRetired());
		assertPropPresent("conceptSource");
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "Some Standardized Terminology: 127cd4689 (cd4died term2)";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_9.CONCEPT_REFERENCE_TERM_UUID;
	}
	
}
