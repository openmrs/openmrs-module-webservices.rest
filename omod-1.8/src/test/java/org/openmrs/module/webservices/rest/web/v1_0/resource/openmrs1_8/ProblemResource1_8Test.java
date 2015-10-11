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
import org.openmrs.activelist.Problem;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ProblemResource1_8;

public class ProblemResource1_8Test extends BaseDelegatingResourceTest<ProblemResource1_8, Problem> {
	
	private static final String ACTIVE_LIST_INITIAL_XML = "customActiveListTest.xml";
	
	@Before
	public void init() throws Exception {
		executeDataSet(ACTIVE_LIST_INITIAL_XML);
	}
	
	@Override
	public Problem newObject() {
		Problem problem = Context.getPatientService().getProblem(2);
		problem.setUuid(getUuidProperty());
		return problem;
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("person");
		assertPropPresent("problem");
		assertPropPresent("activeListType");
		assertPropEquals("modifier", getObject().getModifier());
		assertPropEquals("comments", getObject().getComments());
		assertPropEquals("sortWeight", getObject().getSortWeight());
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
		assertPropPresent("problem");
		assertPropPresent("activeListType");
		assertPropEquals("modifier", getObject().getModifier());
		assertPropEquals("comments", getObject().getComments());
		assertPropEquals("sortWeight", getObject().getSortWeight());
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
		return RestTestConstants1_8.PROBLEM_UUID;
	}
	
	@Test
	public void asRepresentation_shouldReturnProperlyEncodedValues() throws Exception {
		
		Problem problem = getObject();
		
		Concept concept = Context.getConceptService().getConceptByUuid("a09ab2c5-878e-4905-b25d-5784167d0216");
		problem.setProblem(concept);
		
		SimpleObject rep = (SimpleObject) getResource().asRepresentation(getObject(), Representation.DEFAULT);
		Assert.assertTrue(rep.keySet().contains("problem"));
		rep = (SimpleObject) rep.get("problem");
		Assert.assertEquals("problem", concept.getUuid(), rep.get("uuid"));
	}
}
