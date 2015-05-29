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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.springframework.util.Assert;

public class DrugResource1_8Test extends BaseDelegatingResourceTest<DrugResource1_8, Drug> {
	
	@Override
	public Drug newObject() {
		return Context.getConceptService().getDrugByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("retired", getObject().isRetired());
		assertPropEquals("doseStrength", getObject().getDoseStrength());
		assertPropEquals("maximumDailyDose", getObject().getMaximumDailyDose());
		assertPropEquals("minimumDailyDose", getObject().getMinimumDailyDose());
		assertPropEquals("units", getObject().getUnits());
		assertPropEquals("combination", getObject().getCombination());
		assertPropPresent("concept");
		assertPropPresent("route");
		assertPropPresent("dosageForm");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("retired", getObject().isRetired());
		assertPropEquals("doseStrength", getObject().getDoseStrength());
		assertPropEquals("maximumDailyDose", getObject().getMaximumDailyDose());
		assertPropEquals("minimumDailyDose", getObject().getMinimumDailyDose());
		assertPropEquals("units", getObject().getUnits());
		assertPropEquals("combination", getObject().getCombination());
		assertPropPresent("concept");
		assertPropPresent("route");
		assertPropPresent("dosageForm");
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "Aspirin";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_8.DRUG_UUID;
	}
	
	/**
	 * Tests {@link BaseDelegatingResource#setConvertedProperties(Object, java.util.Map, DelegatingResourceDescription, boolean)}
	 */
	@Test
	public void setConvertedProperties_shouldAllowSettingANullValue() {
		DrugResource1_8 resource = new DrugResource1_8();
		Drug drug = new Drug();
		drug.setRoute(new Concept());
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("route", null);
		resource.setConvertedProperties(drug, propertyMap, resource.getUpdatableProperties(), false);
		Assert.isNull(drug.getRoute());
	}

	
}
