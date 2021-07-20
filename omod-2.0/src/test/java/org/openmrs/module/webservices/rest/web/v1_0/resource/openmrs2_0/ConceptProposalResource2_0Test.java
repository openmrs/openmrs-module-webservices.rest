/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import org.junit.Before;
import org.openmrs.ConceptProposal;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.util.OpenmrsConstants;

public class ConceptProposalResource2_0Test extends BaseDelegatingResourceTest<ConceptProposalResource2_0, ConceptProposal> {

	private static ConceptProposal proposal;

	@Before
	public void before() throws Exception {
		proposal = new ConceptProposal();
		proposal.setOriginalText("original text");
		proposal.setFinalText("final text");
		proposal.setState(OpenmrsConstants.CONCEPT_PROPOSAL_UNMAPPED);
		proposal.setComments("comments");
		proposal.setMappedConcept(Context.getConceptService().getConceptByUuid(RestTestConstants1_8.CONCEPT_UUID));
		Context.getConceptService().saveConceptProposal(proposal);
	}

	@Override
	public ConceptProposal newObject() {
		return proposal;
	}

	@Override
	public String getDisplayProperty() {
		return null;
	}

	@Override
	public String getUuidProperty() {
		return proposal.getUuid();
	}
}
