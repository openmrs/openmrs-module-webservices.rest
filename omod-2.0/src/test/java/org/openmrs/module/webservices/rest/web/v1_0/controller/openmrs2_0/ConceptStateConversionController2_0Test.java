/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_0;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptStateConversion;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ConceptStateConversionController2_0Test extends MainResourceControllerTest {

	private static String uuid;

	@Before
	public void setUp() {
		Concept concept = Context.getConceptService().getConceptByUuid(RestTestConstants1_8.CONCEPT2_UUID);
		ProgramWorkflow workflow = Context.getProgramWorkflowService().getWorkflowByUuid(RestTestConstants1_8.WORKFLOW_UUID);

		ProgramWorkflowState state = new ProgramWorkflowState();
		state.setConcept(concept);
		state.setInitial(true);
		state.setTerminal(false);

		workflow.addState(state);
		Context.getProgramWorkflowService().saveProgram(workflow.getProgram());

		Context.flushSession();

		ConceptStateConversion conceptStateConversion = new ConceptStateConversion();
		conceptStateConversion.setConcept(concept);
		conceptStateConversion.setProgramWorkflow(workflow);
		conceptStateConversion.setProgramWorkflowState(state);

		Context.getProgramWorkflowService().saveConceptStateConversion(conceptStateConversion);
		uuid = conceptStateConversion.getUuid();
	}

	@Override
	public String getURI() {
		return "stateconversion";
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public long getAllCount() {
		return 2;
	}

	@Test
	public void shouldCreateStateConversion() throws Exception {
		Concept concept = Context.getConceptService().getConceptByUuid("0955b484-b364-43dd-909b-1fa3655eaad2");
		ProgramWorkflow workflow = Context.getProgramWorkflowService().getWorkflowByUuid(RestTestConstants1_8.WORKFLOW_UUID);

		ProgramWorkflowState state = new ProgramWorkflowState();
		state.setConcept(concept);
		state.setInitial(true);
		state.setTerminal(false);

		workflow.addState(state);
		Context.getProgramWorkflowService().saveProgram(workflow.getProgram());

		Context.flushSession();

		String json =
				"{\"concept\": \"" + concept.getUuid() + "\",\"programWorkflow\": \"" + workflow.getUuid()
						+ "\",\"programWorkflowState\": \"" + state.getUuid() + "\"}";

		SimpleObject newStateConversion = deserialize(handle(newPostRequest(getURI(), json)));

		assertNotNull(newStateConversion);
		String uuid = newStateConversion.get("uuid");

		ConceptStateConversion createdStateConversion = Context.getProgramWorkflowService().getConceptStateConversionByUuid(uuid);
		assertEquals(workflow.getUuid(), createdStateConversion.getProgramWorkflow().getUuid());
		assertEquals(state.getUuid(), createdStateConversion.getProgramWorkflowState().getUuid());
		assertEquals(concept.getUuid(), createdStateConversion.getProgramWorkflow().getConcept().getUuid());
	}

	@Test
	public void shouldPurgeStateConversion() throws Exception {
		Concept concept = Context.getConceptService().getConceptByUuid("0955b484-b364-43dd-909b-1fa3655eaad2");
		ProgramWorkflow workflow = Context.getProgramWorkflowService().getWorkflowByUuid(RestTestConstants1_8.WORKFLOW_UUID);

		ProgramWorkflowState state = new ProgramWorkflowState();
		state.setConcept(concept);
		state.setInitial(true);
		state.setTerminal(false);

		workflow.addState(state);
		Context.getProgramWorkflowService().saveProgram(workflow.getProgram());
		Context.flushSession();

		ConceptStateConversion conceptStateConversion = new ConceptStateConversion();
		conceptStateConversion.setConcept(concept);
		conceptStateConversion.setProgramWorkflow(workflow);
		conceptStateConversion.setProgramWorkflowState(state);
		Context.getProgramWorkflowService().saveConceptStateConversion(conceptStateConversion);

		handle(newDeleteRequest(getURI() + "/" + conceptStateConversion.getUuid(), new Parameter("purge", "true")));

		assertNull(Context.getProgramWorkflowService().getConceptStateConversionByUuid(conceptStateConversion.getUuid()));
	}
}