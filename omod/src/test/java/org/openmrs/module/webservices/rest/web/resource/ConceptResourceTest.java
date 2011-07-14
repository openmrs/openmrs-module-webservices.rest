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
package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.module.webservices.rest.web.v1_0.resource.ConceptResource;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ConceptResourceTest extends BaseDelegatingResourceTest<ConceptResource, Concept> {
	
	@Override
	public Concept newObject() {
		return Context.getConceptService().getConceptByUuid(ResourceTestConstants.CONCEPT_UUID);
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("display", getObject().getDisplayString());
		assertEquals("retired", getObject().getRetired());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertContains("name");
		assertContains("datatype");
		assertContains("conceptClass");
		assertEquals("set", getObject().getSet());
		assertEquals("version", getObject().getVersion());
		assertEquals("retired", getObject().getRetired());
		assertContains("names");
		assertContains("descriptions");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertContains("name");
		assertContains("datatype");
		assertContains("conceptClass");
		assertEquals("set", getObject().getSet());
		assertEquals("version", getObject().getVersion());
		assertEquals("retired", getObject().getRetired());
		assertContains("names");
		assertContains("descriptions");
		assertContains("auditInfo");
	}
	
}
