/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;

/**
 * Unit tests for {@link ConceptResource1_8#getShortName(Concept)}.
 */
public class ConceptResource1_8GetShortNameTest extends BaseModuleWebContextSensitiveTest {

	/**
	 * @verifies return the short name for the concept in the current locale
	 */
	@Test
	public void getShortName_shouldReturnShortNameWhenDefinedForCurrentLocale() {
		Concept concept = new Concept();
		ConceptName shortName = new ConceptName("BP", Context.getLocale());
		shortName.setConceptNameType(ConceptNameType.SHORT);
		concept.addName(shortName);

		ConceptName result = ConceptResource1_8.getShortName(concept);

		assertNotNull(result);
		assertEquals("BP", result.getName());
	}

	/**
	 * @verifies return null when no short name is defined for the current locale
	 */
	@Test
	public void getShortName_shouldReturnNullWhenNoShortNameDefinedForCurrentLocale() {
		Concept concept = new Concept();
		ConceptName fullySpecified = new ConceptName("Blood Pressure", Context.getLocale());
		fullySpecified.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
		concept.addName(fullySpecified);

		ConceptName result = ConceptResource1_8.getShortName(concept);

		assertNull(result);
	}

	/**
	 * @verifies not fall back to a parent locale when short name exists only in a different locale
	 */
	@Test
	public void getShortName_shouldReturnNullWhenShortNameExistsOnlyInDifferentLocale() {
		Concept concept = new Concept();
		// Add a short name in French; current locale is English
		ConceptName shortNameFr = new ConceptName("TA", new Locale("fr"));
		shortNameFr.setConceptNameType(ConceptNameType.SHORT);
		concept.addName(shortNameFr);

		// Ensure current locale is English so it differs from the French short name
		Context.setLocale(new Locale("en"));

		ConceptName result = ConceptResource1_8.getShortName(concept);

		assertNull(result, "getShortName should not fall back to a different locale");
	}
}
