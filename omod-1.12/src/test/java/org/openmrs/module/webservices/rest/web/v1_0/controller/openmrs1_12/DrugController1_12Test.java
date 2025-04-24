/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_12;

import static org.junit.Assert.assertEquals;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

/**
 * Integration tests for the Drug resource for OpenMRS 1.12
 */
public class DrugController1_12Test extends MainResourceControllerTest {
	
	@Override
	public String getURI() {
		return "drug";
	}
	
	@Override
	public String getUuid() {
		return "3cfcf118-931c-46f7-8ff6-7b876f0d4202";
	}
	
	@Override
	public long getAllCount() {
		return conceptService.getAllDrugs(false).size();
	}
	
	private ConceptService conceptService;
	
	protected static final String DRUG_SEARCH_TEST_DATA = "org/openmrs/api/include/ConceptServiceTest-drugSearch.xml";
	
	@Before
	public void init() throws Exception {
		conceptService = Context.getConceptService();
	}
	
	@Test
	public void shouldIncludeStrengthInDefaultRepresentation() throws Exception {
		executeDataSet(DRUG_SEARCH_TEST_DATA);
		String drugUuid = "8e2323fa-0fa0-461f-9b59-6765997d849d";
		Drug drug = conceptService.getDrugByUuid(drugUuid);
		drug.setStrength("full");
		conceptService.saveDrug(drug);
		SimpleObject result = deserialize(handle(newGetRequest(getURI() + "/" + drugUuid)));
		Assert.assertThat("full", Matchers.is(PropertyUtils.getProperty(result, "strength")));
		Assert.assertNull(PropertyUtils.getProperty(result, "route"));
		Assert.assertNull(PropertyUtils.getProperty(result, "units"));
	}
	
	@Test
	public void shouldIncludeStrengthInFullRepresentation() throws Exception {
		executeDataSet(DRUG_SEARCH_TEST_DATA);
		String drugUuid = "8e2323fa-0fa0-461f-9b59-6765997d849d";
		Drug drug = conceptService.getDrugByUuid(drugUuid);
		drug.setStrength("moderate");
		conceptService.saveDrug(drug);
		SimpleObject result = deserialize(handle(newGetRequest(getURI() + "/" + drugUuid, new Parameter("v", "full"))));
		Assert.assertThat("moderate", Matchers.is(PropertyUtils.getProperty(result, "strength")));
		Assert.assertNull(PropertyUtils.getProperty(result, "route"));
		Assert.assertNull(PropertyUtils.getProperty(result, "units"));
	}
	
	@Test
	public void shouldSetStrengthWhenCreatingDrug() throws Exception {
		executeDataSet(DRUG_SEARCH_TEST_DATA);
		SimpleObject drug = new SimpleObject();
		drug.add("name", "newDrug");
		drug.add("concept", "25f83cd6-64e9-4e07-a5f9-364d3b14a43e");
		drug.add("combination", "false");
		drug.add("strength", "low");
		SimpleObject result = deserialize(handle(newPostRequest(getURI(), drug)));
		Assert.assertThat("low", Matchers.is(PropertyUtils.getProperty(result, "strength")));
	}
}
