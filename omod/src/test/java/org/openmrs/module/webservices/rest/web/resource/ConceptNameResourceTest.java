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

import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ConceptNameResourceTest extends BaseDelegatingResourceTest<ConceptNameResource, ConceptName> {
	
	@Override
	public ConceptName newObject() {
		return Context.getConceptService().getConceptByUuid(ResourceTestConstants.CONCEPT_UUID).getName();
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("display", getResource().getDisplayString(getObject()));
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("name", getObject().getName());
		assertEquals("locale", getObject().getLocale());
		assertEquals("localePreferred", getObject().getLocalePreferred());
		assertEquals("conceptNameType", getObject().getConceptNameType());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		assertEquals("uuid", getObject().getUuid());
		assertEquals("name", getObject().getName());
		assertEquals("locale", getObject().getLocale());
		assertEquals("localePreferred", getObject().getLocalePreferred());
		assertEquals("conceptNameType", getObject().getConceptNameType());
		assertContains("auditInfo");
	}
	
}
