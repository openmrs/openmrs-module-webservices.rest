/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.Patient;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.api.SchemaIntrospectionService;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;

/**
 * Tests for {@link SchemaIntrospectionController}.
 */
public class SchemaIntrospectionControllerTest {
	
	@Mock
	private RestService restService;
	
	@Mock
	private SchemaIntrospectionService schemaIntrospectionService;
	
	@InjectMocks
	private SchemaIntrospectionController controller;
	
	private Resource mockResource;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockResource = mock(Resource.class);
	}
	
	/**
	 * @see SchemaIntrospectionController#getResourceProperties(String)
	 */
	@Test
	public void getResourceProperties_shouldReturnPropertiesForValidResource() throws Exception {
		// Setup mock behaviors
		when(restService.getResourceByName(RestConstants.VERSION_1 + "/patient")).thenReturn(mockResource);
		when(schemaIntrospectionService.getDelegateType(mockResource)).thenReturn((Class) Patient.class);
		
		// Setup mock properties
		Map<String, String> mockProperties = new HashMap<String, String>();
		mockProperties.put("uuid", "String");
		mockProperties.put("patientId", "Integer");
		mockProperties.put("gender", "String");
		mockProperties.put("voided", "Boolean");
		
		when(schemaIntrospectionService.discoverAvailableProperties(Patient.class)).thenReturn(mockProperties);
		
		// Call the controller method
		SimpleObject result = controller.getResourceProperties("patient");
		
		// Verify the result
		assertThat(result, is(notNullValue()));
		assertThat((String) result.get("resourceName"), is(equalTo("patient")));
		assertThat((String) result.get("delegateType"), is(equalTo(Patient.class.getName())));
		
		// Verify the properties were included
		@SuppressWarnings("unchecked")
		Map<String, String> resultProperties = (Map<String, String>) result.get("discoverableProperties");
		assertThat(resultProperties, is(notNullValue()));
		assertThat(resultProperties.size(), is(equalTo(4)));
		assertThat(resultProperties.get("uuid"), is(equalTo("String")));
		assertThat(resultProperties.get("patientId"), is(equalTo("Integer")));
		assertThat(resultProperties.get("gender"), is(equalTo("String")));
		assertThat(resultProperties.get("voided"), is(equalTo("Boolean")));
	}
	
	/**
	 * @see SchemaIntrospectionController#getResourceProperties(String)
	 */
	@Test(expected = ObjectNotFoundException.class)
	public void getResourceProperties_shouldThrowExceptionForNonExistentResource() throws Exception {
		// Setup mock behavior for non-existent resource
		when(restService.getResourceByName(anyString())).thenReturn(null);
		
		// This should throw ObjectNotFoundException
		controller.getResourceProperties("nonexistent");
	}
	
	/**
	 * @see SchemaIntrospectionController#getResourceProperties(String)
	 */
	@Test(expected = ObjectNotFoundException.class)
	public void getResourceProperties_shouldThrowExceptionWhenDelegateTypeCannotBeDetermined() throws Exception {
		// Setup mock behaviors where resource is found but delegate type isn't
		when(restService.getResourceByName(RestConstants.VERSION_1 + "/patient")).thenReturn(mockResource);
		when(schemaIntrospectionService.getDelegateType(mockResource)).thenReturn(null);
		
		// This should throw ObjectNotFoundException
		controller.getResourceProperties("patient");
	}
	
	/**
	 * @see SchemaIntrospectionController#getResourceProperties(String)
	 */
	@Test(expected = ObjectNotFoundException.class)
	public void getResourceProperties_shouldThrowExceptionWhenResourceLookupFails() throws Exception {
		// Setup mock behavior to throw an exception during resource lookup
		when(restService.getResourceByName(anyString())).thenThrow(new RuntimeException("Resource lookup failed"));
		
		// This should throw ObjectNotFoundException
		controller.getResourceProperties("patient");
	}
}