package org.openmrs.module.webservices.rest.web.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

public class RestServiceTest extends BaseModuleWebContextSensitiveTest {
	
	private RestService service;
	
	@Before
	public void beforeEachTest() {
		service = Context.getService(RestService.class);
	}
	
	/**
	 * @see RestService#getRepresentation(String)
	 * @verifies get ref representation when specified
	 */
	@Test
	public void getRepresentation_shouldGetRefRepresentationWhenSpecified() throws Exception {
		Assert.assertEquals(Representation.REF, service.getRepresentation(RestConstants.REPRESENTATION_REF));
	}
	
	/**
	 * @see RestService#getRepresentation(String)
	 * @verifies get default representation when specified
	 */
	@Test
	public void getRepresentation_shouldGetDefaultRepresentationWhenSpecified() throws Exception {
		Assert.assertEquals(Representation.DEFAULT, service.getRepresentation(RestConstants.REPRESENTATION_DEFAULT));
	}
	
	/**
	 * @see RestService#getRepresentation(String)
	 * @verifies get full representation when specified
	 */
	@Test
	public void getRepresentation_shouldGetFullRepresentationWhenSpecified() throws Exception {
		Assert.assertEquals(Representation.FULL, service.getRepresentation(RestConstants.REPRESENTATION_FULL));
	}
	
	/**
	 * @see RestService#getRepresentation(String)
	 * @verifies get a named representation when specified
	 */
	@Test
	public void getRepresentation_shouldGetANamedRepresentationWhenSpecified() throws Exception {
		String namedRep = "someName";
		Representation representation = service.getRepresentation(namedRep);
		Assert.assertTrue(representation instanceof NamedRepresentation);
		Assert.assertEquals(namedRep, representation.getRepresentation());
	}
}
