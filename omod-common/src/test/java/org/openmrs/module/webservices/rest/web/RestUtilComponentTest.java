package org.openmrs.module.webservices.rest.web;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.validation.ValidationException;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

public class RestUtilComponentTest extends BaseModuleWebContextSensitiveTest {
	
	@Test
	public void wrapValidationErrorResponse_shouldCreateSimpleObjectFromErrorsObject() {
		
		Errors ex = new BindException(new Person(), "");
		ex.rejectValue("birthdate", "field.error.message");
		ex.reject("global.error.message");
		
		SimpleObject result = RestUtil.wrapValidationErrorResponse(new ValidationException("some message", ex));
		SimpleObject errors = (SimpleObject) result.get("error");
		Assert.assertEquals("webservices.rest.error.invalid.submission", errors.get("code"));
		
		List<SimpleObject> globalErrors = (List<SimpleObject>) errors.get("globalErrors");
		Assert.assertEquals(1, globalErrors.size());
		Assert.assertEquals("global.error.message", globalErrors.get(0).get("code"));
		
		SimpleObject fieldErrors = (SimpleObject) errors.get("fieldErrors");
		List<SimpleObject> birthdateFieldErrors = (List<SimpleObject>) fieldErrors.get("birthdate");
		Assert.assertEquals("field.error.message", birthdateFieldErrors.get(0).get("code"));
		
	}
	
	@Test
	public void wrapValidationErrorResponse_shouldIncludeGlobalAndFieldErrorObjectsEvenIfEmpty() {
		
		Errors ex = new BindException(new Person(), "");
		
		SimpleObject result = RestUtil.wrapValidationErrorResponse(new ValidationException("some message", ex));
		SimpleObject errors = (SimpleObject) result.get("error");
		Assert.assertEquals("webservices.rest.error.invalid.submission", errors.get("code"));
		
		Assert.assertEquals(0, ((List<SimpleObject>) errors.get("globalErrors")).size());
		Assert.assertNotNull(errors.get("fieldErrors"));
		
	}
}
