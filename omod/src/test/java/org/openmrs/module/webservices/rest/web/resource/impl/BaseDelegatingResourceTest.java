package org.openmrs.module.webservices.rest.web.resource.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public abstract class BaseDelegatingResourceTest<T> extends BaseModuleContextSensitiveTest {
	
	public abstract BaseDelegatingResource<T> getResource();
	
	public abstract T getObject();
	
	/**
	 * @see BaseDelegatingResource#asRepresentation(T,Representation)
	 * @verifies return RefRepresentation
	 */
	@Test
	public void asRepresentation_shouldReturnRefRepresentation() throws Exception {
		T object = getObject();
		Assert.assertNotNull("Object must not be null", object);
		
		Object result = getResource().asRepresentation(object, Representation.REF);
		Assert.assertNotNull("Result must not be null", result);
	}
	
	/**
	 * @see BaseDelegatingResource#asRepresentation(T,Representation)
	 * @verifies return DefaultRepresentation
	 */
	@Test
	public void asRepresentation_shouldReturnDefaultRepresentation() throws Exception {
		T object = getObject();
		Assert.assertNotNull("Object must not be null", object);
		
		Object result = getResource().asRepresentation(object, Representation.DEFAULT);
		Assert.assertNotNull("Result must not be null", result);
	}
	
	/**
	 * @see BaseDelegatingResource#asRepresentation(T,Representation)
	 * @verifies return FullRepresentation
	 */
	@Test
	public void asRepresentation_shouldReturnFullRepresentation() throws Exception {
		T object = getObject();
		Assert.assertNotNull("Object must not be null", object);
		
		Object result = getResource().asRepresentation(object, Representation.FULL);
		Assert.assertNotNull("Result must not be null", result);
	}
	
}
