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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.activelist.Allergy;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.AllergyResource1_8;

public class AllergyResource1_8Test extends BaseDelegatingResourceTest<AllergyResource1_8, Allergy> {
	
	private static final String ACTIVE_LIST_INITIAL_XML = "customActiveListTest.xml";
	
	@Before
	public void init() throws Exception {
		executeDataSet(ACTIVE_LIST_INITIAL_XML);
	}
	
	@Override
	public Allergy newObject() {
		Allergy allergy = Context.getPatientService().getAllergy(1);
		allergy.setUuid(getUuidProperty());
		return allergy;
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("person");
		assertPropPresent("allergen");
		assertPropEquals("allergyType", getObject().getAllergyType());
		assertPropPresent("activeListType");
		assertPropEquals("severity", getObject().getSeverity());
		assertPropEquals("comments", getObject().getComments());
		assertPropEquals("reaction", getObject().getReaction());
		assertPropPresent("startDate");
		assertPropPresent("endDate");
		assertPropPresent("startObs");
		assertPropPresent("stopObs");
		assertPropEquals("voided", getObject().getVoided());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropPresent("person");
		assertPropPresent("allergen");
		assertPropEquals("allergyType", getObject().getAllergyType());
		assertPropPresent("activeListType");
		assertPropEquals("severity", getObject().getSeverity());
		assertPropEquals("comments", getObject().getComments());
		assertPropEquals("reaction", getObject().getReaction());
		assertPropPresent("startDate");
		assertPropPresent("endDate");
		assertPropPresent("startObs");
		assertPropPresent("stopObs");
		assertPropEquals("voided", getObject().getVoided());
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "FOOD ASSISTANCE";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_8.ALLERGY_UUID;
	}
	
	@Test
	public void asRepresentation_shouldReturnProperlyEncodedValues() throws Exception {
		Allergy allergy = getObject();
		
		Concept concept = Context.getConceptService().getConceptByUuid("a09ab2c5-878e-4905-b25d-5784167d0216");
		allergy.setAllergen(concept);
		
		SimpleObject rep = (SimpleObject) getResource().asRepresentation(getObject(), Representation.DEFAULT);
		Assert.assertTrue(rep.keySet().contains("allergen"));
		rep = (SimpleObject) rep.get("allergen");
		Assert.assertEquals("allergen", concept.getUuid(), rep.get("uuid"));
	}
}
