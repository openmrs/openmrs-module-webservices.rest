package org.openmrs.module.webservices.rest.validation;

import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.module.webservices.validation.ValidateUtil;
import org.openmrs.test.Verifies;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

public class ValidateUtilTest extends BaseModuleWebContextSensitiveTest {

    // we are not supporting this validation against 1.8, so when running against 1.8 no exception should be thrown


    /**
	 * @see {@link org.openmrs.validator.ValidateUtil#validate(Object)}
	 */
    @Test
	@Verifies(value = "should not throw exception", method = "validate(Object)")
	public void validate_shouldNotThrowValidationExceptionButShouldNotFail() throws Exception {

        // we are not supporting this validation against 1.8, so when running against 1.8 when passing in an
        // invalid object, no exception should be thrown

		Location loc = new Location();
		ValidateUtil.validate(loc);
	}
	
}
