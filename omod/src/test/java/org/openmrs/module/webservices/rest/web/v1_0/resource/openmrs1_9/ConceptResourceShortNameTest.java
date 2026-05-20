/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;

/**
 * Tests for the shortName property on the Concept resource.
 */
public class ConceptResourceShortNameTest extends BaseModuleWebContextSensitiveTest {

	private ConceptResource1_9 getResource() {
		return new ConceptResource1_9();
	}

	/**
	 * @verifies include shortName key in the full representation output
	 */
	@Test
	public void shortName_shouldBePresentInFullRepresentation() throws Exception {
		Concept concept = Context.getConceptService().getConceptByUuid(RestTestConstants1_8.CONCEPT_UUID);
		ConceptResource1_9 resource = getResource();

		SimpleObject fullRep = resource.asRepresentation(concept, Representation.FULL);

		assertThat("shortName key should be present in full representation", fullRep, hasKey("shortName"));
	}

	/**
	 * @verifies return the correct short name value when concept has a short name and full
	 *           representation is requested
	 */
	@Test
	public void shortName_shouldReturnCorrectValueInFullRepresentationWhenShortNameIsDefined() throws Exception {
		Concept concept = Context.getConceptService().getConceptByUuid(RestTestConstants1_8.CONCEPT_UUID);
		ConceptName shortName = new ConceptName("YS", new Locale("en"));
		shortName.setConceptNameType(ConceptNameType.SHORT);
		concept.addName(shortName);
		concept = Context.getConceptService().saveConcept(concept);

		ConceptResource1_9 resource = getResource();
		SimpleObject fullRep = resource.asRepresentation(concept, Representation.FULL);

		assertThat("shortName key should be present in full representation", fullRep, hasKey("shortName"));
		assertNotNull(fullRep.get("shortName"));
	}

	/**
	 * @verifies resolve shortName via a custom representation spec
	 */
	@Test
	public void shortName_shouldBeResolvableViaCustomRepresentation() throws Exception {
		Concept concept = Context.getConceptService().getConceptByUuid(RestTestConstants1_8.CONCEPT_UUID);
		ConceptName shortName = new ConceptName("YS", new Locale("en"));
		shortName.setConceptNameType(ConceptNameType.SHORT);
		concept.addName(shortName);
		concept = Context.getConceptService().saveConcept(concept);

		ConceptResource1_9 resource = getResource();
		SimpleObject customRep = resource.asRepresentation(concept,
		    new CustomRepresentation("uuid,shortName"));

		assertTrue(customRep.containsKey("uuid"), "uuid should be present in custom representation");
		assertTrue(customRep.containsKey("shortName"), "shortName should be resolvable via custom representation");
		assertNotNull(customRep.get("shortName"));
	}

	/**
	 * @verifies not include shortName in the default representation
	 */
	@Test
	public void shortName_shouldNotBePresentInDefaultRepresentation() throws Exception {
		Concept concept = Context.getConceptService().getConceptByUuid(RestTestConstants1_8.CONCEPT_UUID);
		ConceptResource1_9 resource = getResource();

		SimpleObject defaultRep = resource.asRepresentation(concept, Representation.DEFAULT);

		assertThat("shortName should not be present in default representation", defaultRep, not(hasKey("shortName")));
	}

	/**
	 * @verifies not include shortName in the ref representation
	 */
	@Test
	public void shortName_shouldNotBePresentInRefRepresentation() throws Exception {
		Concept concept = Context.getConceptService().getConceptByUuid(RestTestConstants1_8.CONCEPT_UUID);
		ConceptResource1_9 resource = getResource();

		SimpleObject refRep = resource.asRepresentation(concept, Representation.REF);

		assertThat("shortName should not be present in ref representation", refRep, not(hasKey("shortName")));
	}
}
