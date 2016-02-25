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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_11;

import org.openmrs.DrugIngredient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class DrugIngredientResource1_11Test extends BaseDelegatingResourceTest<DrugIngredientResource1_11, DrugIngredient> {
	
	@Override
	public DrugIngredient newObject() {
		DrugIngredient ingredient = new DrugIngredient();
		ingredient.setIngredient(Context.getConceptService().getConcept(3));
		ingredient.setUuid(getUuidProperty());
		ingredient.setStrength(2d);
		ingredient.setUnits(Context.getConceptService().getConcept(8));
		return ingredient;
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("uuid", getObject().getUuid());
		assertPropEquals("strength", getObject().getStrength());
		assertPropPresent("ingredient");
		assertPropPresent("units");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("uuid", getObject().getUuid());
		assertPropEquals("strength", getObject().getStrength());
		assertPropPresent("ingredient");
		assertPropPresent("units");
	}
	
	@Override
	public String getUuidProperty() {
		return "e322c90c-7cd9-4e25-bdff-f21d387759b6";
	}

	@Override
	public String getDisplayProperty() {
		return "COUGH SYRUP";
	}
}