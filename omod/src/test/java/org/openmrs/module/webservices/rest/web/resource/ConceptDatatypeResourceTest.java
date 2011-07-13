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

import org.openmrs.ConceptDatatype;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ConceptDatatypeResourceTest extends BaseDelegatingResourceTest<ConceptDatatypeResource, ConceptDatatype> {
	
	@Override
	public ConceptDatatype newObject() {
		return Context.getConceptService().getConceptByUuid(ResourceTestConstants.CONCEPT_UUID).getDatatype();
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertContains("display"); //no getter
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("name", getObject().getName());
		assertEquals("description", getObject().getDescription());
		assertEquals("hl7Abbreviation", getObject().getHl7Abbreviation());
		assertEquals("retired", getObject().getRetired());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("name", getObject().getName());
		assertEquals("description", getObject().getDescription());
		assertEquals("hl7Abbreviation", getObject().getHl7Abbreviation());
		assertEquals("retired", getObject().getRetired());
		assertContains("auditInfo");
	}
	
}
