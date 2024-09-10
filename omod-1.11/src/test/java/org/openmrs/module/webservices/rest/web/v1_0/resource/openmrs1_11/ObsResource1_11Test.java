/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_11;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.ConceptNumeric;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ObsResource1_11Test extends BaseDelegatingResourceTest<ObsResource1_11, Obs> {
	
	@Override
	public Obs newObject() {
		return Context.getObsService().getObsByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("formFieldPath");
		assertPropPresent("formFieldNamespace");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropPresent("formFieldPath");
		assertPropPresent("formFieldNamespace");
	}
	
	@Override
	public String getDisplayProperty() {
		return "WEIGHT (KG): 50.0";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_9.OBS2_UUID;
	}

	@Test
	public void asRepresentation_shouldReturnNumericValueBasedOnConceptNumericAllowDecimal() {
		Obs obs = getObject();
		obs.setValueNumeric(20.0);
		ConceptNumeric cn = Context.getConceptService().getConceptNumeric(5497);
		obs.setConcept(cn);
		cn.setAllowDecimal(true);
		SimpleObject rep = getResource().asRepresentation(getObject(), Representation.DEFAULT);
		Object value = rep.get("value");
		Assert.assertEquals(20.0, value);
		Assert.assertEquals(Double.class, value.getClass());
		cn.setAllowDecimal(false);
		rep = getResource().asRepresentation(getObject(), Representation.DEFAULT);
		value = rep.get("value");
		Assert.assertEquals(20, value);
		Assert.assertEquals(Integer.class, value.getClass());
	}
}
