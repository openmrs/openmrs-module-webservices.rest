/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;

/**
 * {@link ConversionUtil#isRefusedByARule(Object)} with no session open. Deliberately not a
 * {@code BaseModuleWebContextSensitiveTest}: the point is the absence of a service context, which
 * such a test always provides.
 * <p>
 * The rules are registered in the service context, so asking them needs one. Display getters are
 * plain methods, though, and unit tests call them on objects they built themselves - this module's
 * own {@code DrugOrderSubclassHandler1_10Test} does, and so may any module depending on this one.
 * Throwing there would make every guarded getter require a session to run.
 */
public class ConversionUtilNoSessionTest {

	@Test
	public void isRefusedByARule_shouldReturnFalseWithoutASessionRatherThanThrow() {
		assertFalse(Context.isSessionOpen(), "this test is meaningless with a session open");

		Drug transientDrug = new Drug();
		transientDrug.setName("Aspirin");

		assertFalse(ConversionUtil.isRefusedByARule(transientDrug));
	}

	@Test
	public void isRefusedByARule_shouldReturnFalseForANonDomainObjectWithoutASession() {
		assertFalse(ConversionUtil.isRefusedByARule("not a domain object"));
		assertFalse(ConversionUtil.isRefusedByARule(null));
	}
}
