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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_10;

import static junit.framework.Assert.assertEquals;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptSource;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

/**
 * Integration tests for the Order resource
 */
public class DrugController1_10Test extends MainResourceControllerTest {
	
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
	
	protected static final String DRUG_MAPPINGS = "org/openmrs/api/include/ConceptServiceTest-getDrugMappings.xml";
	
	private ConceptService conceptService;
	
	@Before
	public void init() throws Exception {
		conceptService = Context.getConceptService();
	}
	
	@Test
	public void getDrugByMapping_shouldReturnADrugThatMatchesTheCodeAndSourceAndTheBestMapType() throws Exception {
		executeDataSet(DRUG_MAPPINGS);
		final ConceptSource source = conceptService.getConceptSource(2);
		String mapTypeUuids = conceptService.getConceptMapType(1).getUuid() + ","
		        + conceptService.getConceptMapType(2).getUuid();
		SimpleObject results = deserialize(handle(newGetRequest(getURI(), new Parameter("s", "default"), new Parameter(
		        "code", "WGT234"), new Parameter("source", source.getUuid()), new Parameter("mapTypes", mapTypeUuids))));
		
		assertEquals(getUuid(), PropertyUtils.getProperty(Util.getResultsList(results).get(0), "uuid"));
	}
}
