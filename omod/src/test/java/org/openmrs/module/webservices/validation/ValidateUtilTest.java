/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.openmrs.Location;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;
public class ValidateUtilTest extends BaseModuleWebContextSensitiveTest {
	
	// this is only enabled in OpenMRS 1.9 and 1.10, so we include it in the tests for these specific versions
	
	/**
	 * @see {@link org.openmrs.validator.ValidateUtil#validate(Object)}
	 */
	@Test
	public void validate_shouldThrowValidationExceptionIfErrorsOccurDuringValidation() throws Exception {
		
		assertThrows(ValidationException.class, () -> {
			Location loc = new Location();
			ValidateUtil.validate(loc);
		});
	}
	
}
