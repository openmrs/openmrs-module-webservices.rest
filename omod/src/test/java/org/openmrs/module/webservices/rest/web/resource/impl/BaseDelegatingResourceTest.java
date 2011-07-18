/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.resource.impl;

import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.Hyperlink;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

/**
 * Is designed to be extended by classes testing BaseDelegatingResource.
 * 
 * @param <R> resource
 * @param <T> object
 */
public abstract class BaseDelegatingResourceTest<R extends BaseDelegatingResource<T>, T> extends BaseModuleWebContextSensitiveTest {
	
	private T object;
	
	private R resource;
	
	private SimpleObject representation;
	
	/**
	 * Creates an instance of an object that will be used to test the resource.
	 * 
	 * @return the new object
	 */
	public abstract T newObject();
	
	/**
	 * Validates RefRepresentation of the object returned by the resource.
	 * 
	 * @throws Exception
	 */
	public abstract void validateRefRepresentation() throws Exception;
	
	/**
	 * Validates DefaultRepresentation of the object returned by the resource.
	 * 
	 * @throws Exception
	 */
	public abstract void validateDefaultRepresentation() throws Exception;
	
	/**
	 * Validates FullRepresentation of the object returned by the resource.
	 * 
	 * @throws Exception
	 */
	public abstract void validateFullRepresentation() throws Exception;
	
	/**
	 * @return the display property
	 */
	public abstract String getDisplayProperty();
	
	/**
	 * @return the uuid property
	 */
	public abstract String getUuidProperty();
	
	/**
	 * Instantiates BaseDelegatingResource.
	 * 
	 * @return the new resource
	 */
	public R newResource() {
		ParameterizedType t = (ParameterizedType) getClass().getGenericSuperclass();
		@SuppressWarnings("unchecked")
		Class<R> clazz = (Class<R>) t.getActualTypeArguments()[0];
		return Context.getService(RestService.class).getResource(clazz);
	}
	
	public T getObject() {
		if (object == null) {
			object = newObject();
		}
		Assert.assertNotNull("newObject must not return null", object);
		return object;
	}
	
	public SimpleObject getRepresentation() {
		Assert.assertNotNull("representation must not be null", representation);
		return representation;
	}
	
	public R getResource() {
		if (resource == null) {
			resource = newResource();
		}
		Assert.assertNotNull("newResource must not return null", resource);
		return resource;
	}
	
	public SimpleObject newRefRepresentation() throws Exception {
		return (SimpleObject) getResource().asRepresentation(getObject(), Representation.REF);
	}
	
	public SimpleObject newDefaultRepresentation() throws Exception {
		return (SimpleObject) getResource().asRepresentation(getObject(), Representation.DEFAULT);
	}
	
	public SimpleObject getFullRepresentation() throws Exception {
		return (SimpleObject) getResource().asRepresentation(getObject(), Representation.FULL);
	}
	
	public void assertPropEquals(String property, Object value) {
		if (value instanceof Date) {
			value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format((Date) value);
		} else if (value instanceof Locale) {
			value = value.toString();
		}
		Assert.assertEquals(property, value, getRepresentation().get(property));
	}
	
	public void assertPropPresent(String property) {
		Assert.assertTrue(getRepresentation().containsKey(property));
	}
	
	@Test
	public void asRepresentation_shouldReturnValidRefRepresentation() throws Exception {
		representation = newRefRepresentation();
		
		assertPropEquals("uuid", getUuidProperty());
		assertPropEquals("display", getDisplayProperty());
		assertPropPresent("links");
		
		@SuppressWarnings("unchecked")
		List<Hyperlink> links = (List<Hyperlink>) getRepresentation().get("links");
		boolean self = false;
		for (Hyperlink link : links) {
			if (link.getRel().equals("self")) {
				Assert.assertNotNull(link.getUri());
				self = true;
				break;
			}
		}
		Assert.assertTrue(self);
		
		validateRefRepresentation();
	}
	
	@Test
	public void asRepresentation_shouldReturnValidDefaultRepresentation() throws Exception {
		representation = newDefaultRepresentation();
		
		assertPropEquals("uuid", getUuidProperty());
		assertPropPresent("links");
		
		validateDefaultRepresentation();
	}
	
	@Test
	public void asRepresentation_shouldReturnValidFullRepresentation() throws Exception {
		representation = getFullRepresentation();
		
		assertPropEquals("uuid", getUuidProperty());
		assertPropPresent("links");
		
		validateFullRepresentation();
	}
}
