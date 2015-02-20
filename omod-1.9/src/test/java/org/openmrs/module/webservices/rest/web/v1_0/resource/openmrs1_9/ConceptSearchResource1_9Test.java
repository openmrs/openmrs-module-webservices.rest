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
import org.openmrs.ConceptSearchResult;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ConceptSearchResource1_9Test extends BaseDelegatingResourceTest<ConceptSearchResource1_9, ConceptSearchResult> {
	
	@Override
	public ConceptSearchResult newObject() {
		Concept concept = Context.getConceptService().getConceptByUuid(getUuidProperty());
		return new ConceptSearchResult("Yes", concept, concept.getName(), 12d);
		
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		assertPropEquals("display", getDisplayProperty());
		assertPropPresent("concept");
		assertPropPresent("conceptName");
		assertPropNotPresent("resourceVersion");
		assertPropNotPresent("uuid");
		assertPropNotPresent("links");
		assertPropNotPresent("word");
		assertPropNotPresent("transientWeight");
		
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		assertPropEquals("display", getDisplayProperty());
		assertPropPresent("resourceVersion");
		assertPropPresent("concept");
		assertPropPresent("conceptName");
		assertPropNotPresent("uuid");
		assertPropNotPresent("links");
		assertPropNotPresent("word");
		assertPropNotPresent("transientWeight");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		assertPropEquals("display", getDisplayProperty());
		assertPropPresent("resourceVersion");
		assertPropPresent("concept");
		assertPropPresent("conceptName");
		assertPropEquals("word", getObject().getWord());
		assertPropEquals("transientWeight", getObject().getTransientWeight());
		assertPropNotPresent("uuid");
		assertPropNotPresent("links");
	}
	
	@Override
	public String getDisplayProperty() {
		return "YES";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_8.CONCEPT_UUID;
	}
	
}
