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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Allergy;
import org.openmrs.Allergen;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class AllergyResource2_0Test extends
		BaseDelegatingResourceTest<AllergyResource2_0, Allergy> {

	private static final String ACTIVE_LIST_INITIAL_XML = "customAllergyTestData.xml";

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
		assertPropPresent("patient");
		assertPropPresent("allergen");
		assertPropPresent("severity");
		assertPropPresent("reactions");
		assertPropEquals("comment", getObject().getComment());
		assertPropEquals("voided", getObject().getVoided());
	}

	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropPresent("patient");
		assertPropPresent("patient");
		assertPropPresent("allergen");
		assertPropPresent("severity");
		assertPropPresent("reactions");
		assertPropEquals("comment", getObject().getComment());
		assertPropEquals("voided", getObject().getVoided());
		assertPropPresent("auditInfo");
	}

	@Override
	public String getDisplayProperty() {
		return "STAVUDINE LAMIVUDINE AND NEVIRAPINE";
	}

	@Override
	public String getUuidProperty() {
		return RestTestConstants2_0.ALLERGY_UUID;
	}

	@Test
	public void asRepresentation_shouldReturnProperlyEncodedValues()
			throws Exception {
		Allergy allergy = getObject();

		Concept concept = Context.getConceptService().getConceptByUuid(
				"a09ab2c5-878e-4905-b25d-5784167d0216");
		Allergen allergen = new Allergen();
		allergen.setCodedAllergen(concept);
		allergy.setAllergen(allergen);

		SimpleObject rep = getResource().asRepresentation(getObject(),
				Representation.DEFAULT);
		/*
		 * // TODO - api has changed, must be updated
		 * Assert.assertTrue(rep.keySet().contains("allergen")); rep =
		 * (SimpleObject) rep.get("allergen"); Assert.assertEquals("allergen",
		 * concept.getUuid(), rep.get("uuid"));
		 */
	}
}
