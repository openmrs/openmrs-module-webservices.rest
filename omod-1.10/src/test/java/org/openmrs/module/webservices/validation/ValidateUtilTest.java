package org.openmrs.module.webservices.validation;

import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.test.Verifies;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

public class ValidateUtilTest extends BaseModuleWebContextSensitiveTest {

    // this is only enabled in OpenMRS 1.9 and 1.10, so we include it in the tests for these specific versions

	/**
	 * @see {@link org.openmrs.validator.ValidateUtil#validate(Object)}
	 */
	@Test(expected = ValidationException.class)
	@Verifies(value = "should throw ValidationException if errors occur during validation", method = "validate(Object)")
	public void validate_shouldThrowValidationExceptionIfErrorsOccurDuringValidation() throws Exception {
		
		Location loc = new Location();
		ValidateUtil.validate(loc);
	}
	
}
