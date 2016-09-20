/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import org.junit.Before;
import org.openmrs.ConceptAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.RestTestConstants2_0;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ConceptAttributeResource2_0Test extends BaseDelegatingResourceTest<ConceptAttributeResource2_0, ConceptAttribute> {
	
	@Before
	public void before() throws Exception {
		executeDataSet(RestTestConstants2_0.CONCEPT_ATTRIBUTE_DATA_SET);
	}
	
	@Override
	public ConceptAttribute newObject() {
		return Context.getConceptService().getConceptAttributeByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("value", getObject().getValue());
		assertPropPresent("attributeType");
		assertPropEquals("voided", getObject().getVoided());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("value", getObject().getValue());
		assertPropPresent("attributeType");
		assertPropEquals("voided", getObject().getVoided());
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		try {
			return "Joining Date: " + new SimpleDateFormat("yyyy-MM-dd").parse("2011-04-25");
		}
		catch (ParseException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants2_0.CONCEPT_ATTRIBUTE_UUID;
	}
}
