/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.api.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.util.OpenmrsConstants;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link RestServiceImpl}.
 */
public class RestServiceImplTest extends BaseContextMockTest {
	
	@Mock
	RestHelperService restHelperService;
	
	@InjectMocks
	RestService restService = new RestServiceImpl();
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies throw exception if no handler with id
	 */
	@Test
	public void getSearchHandler_shouldThrowExceptionIfNoHandlerWithId() throws Exception {
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig("none", "concept", "1.8.*", new SearchQuery.Builder("Fuzzy search")
		        .withRequiredParameters("q").build());
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		when(restHelperService.getRegisteredSearchHandlers()).thenReturn(Arrays.asList(searchHandler));
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("s", new String[] { "conceptByMapping" });
		
		expectedException.expect(InvalidSearchException.class);
		expectedException.expectMessage("The search with id 'conceptByMapping' for '" + searchConfig.getSupportedResource()
		        + "' resource is not recognized");
		restService.getSearchHandler("concept", parameters);
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies return handler by id if exists
	 */
	@Test
	public void getSearchHandler_shouldReturnHandlerByIdIfExists() throws Exception {
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig("conceptByMapping", "concept", OpenmrsConstants.OPENMRS_VERSION_SHORT,
		        new SearchQuery.Builder("Fuzzy search").withRequiredParameters("q").build());
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		when(restHelperService.getRegisteredSearchHandlers()).thenReturn(Arrays.asList(searchHandler));
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("s", new String[] { "conceptByMapping" });
		SearchHandler searchHandler2 = restService.getSearchHandler("concept", parameters);
		assertThat(searchHandler2, is(searchHandler));
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies throw ambiguous exception if case 1
	 */
	@Test
	public void getSearchHandler_shouldThrowAmbiguousExceptionIfCase1() throws Exception {
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig("conceptByMapping", "concept", OpenmrsConstants.OPENMRS_VERSION_SHORT,
		        new SearchQuery.Builder("description").withRequiredParameters("sourceName", "code").build());
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		SearchConfig searchConfig2 = new SearchConfig("conceptByMapping2", "concept",
		        OpenmrsConstants.OPENMRS_VERSION_SHORT, new SearchQuery.Builder("description").withRequiredParameters(
		            "sourceName", "code").build());
		when(searchHandler2.getSearchConfig()).thenReturn(searchConfig2);
		
		when(restHelperService.getRegisteredSearchHandlers()).thenReturn(Arrays.asList(searchHandler, searchHandler2));
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("sourceName", new String[] { "some name" });
		parameters.put("code", new String[] { "some code" });
		
		expectedException.expect(InvalidSearchException.class);
		expectedException.expectMessage("The search is ambiguous. Please specify s=");
		restService.getSearchHandler("concept", parameters);
	}
	
	public Set<SearchQuery> newParameters(SearchQuery... parameters) {
		return new HashSet<SearchQuery>(Arrays.asList(parameters));
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies return handler if case 2
	 */
	@Test
	public void getSearchHandler_shouldReturnHandlerIfCase2() throws Exception {
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig("conceptByMapping", "concept", OpenmrsConstants.OPENMRS_VERSION_SHORT,
		        new SearchQuery.Builder("description").withRequiredParameters("sourceName").withOptionalParameters("code")
		                .build());
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		SearchConfig searchConfig2 = new SearchConfig("conceptByMapping2", "concept",
		        OpenmrsConstants.OPENMRS_VERSION_SHORT, new SearchQuery.Builder("description").withRequiredParameters(
		            "sourceName").build());
		when(searchHandler2.getSearchConfig()).thenReturn(searchConfig2);
		
		when(restHelperService.getRegisteredSearchHandlers()).thenReturn(Arrays.asList(searchHandler, searchHandler2));
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("sourceName", new String[] { "some name" });
		parameters.put("code", new String[] { "some code" });
		SearchHandler searchHandler3 = restService.getSearchHandler("concept", parameters);
		
		assertThat(searchHandler3, is(searchHandler));
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies throw ambiguous exception if case 3
	 */
	@Test
	public void getSearchHandler_shouldThrowAmbiguousExceptionIfCase3() throws Exception {
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig("conceptByMapping", "concept", OpenmrsConstants.OPENMRS_VERSION_SHORT,
		        new SearchQuery.Builder("description").withRequiredParameters("sourceName").withOptionalParameters("code")
		                .build());
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		SearchConfig searchConfig2 = new SearchConfig("conceptByMapping2", "concept",
		        OpenmrsConstants.OPENMRS_VERSION_SHORT, new SearchQuery.Builder("description").withRequiredParameters(
		            "sourceName").build());
		when(searchHandler2.getSearchConfig()).thenReturn(searchConfig2);
		
		when(restHelperService.getRegisteredSearchHandlers()).thenReturn(Arrays.asList(searchHandler, searchHandler2));
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("sourceName", new String[] { "some name" });
		
		expectedException.expect(InvalidSearchException.class);
		expectedException.expectMessage("The search is ambiguous. Please specify s=");
		restService.getSearchHandler("concept", parameters);
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies return null if too few parameters
	 */
	@Test
	public void getSearchHandler_shouldReturnNullIfTooFewParameters() throws Exception {
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig("conceptByMapping", "concept", OpenmrsConstants.OPENMRS_VERSION_SHORT,
		        new SearchQuery.Builder("description").withRequiredParameters("sourceName").withOptionalParameters("code")
		                .build());
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		SearchConfig searchConfig2 = new SearchConfig("conceptByMapping2", "concept",
		        OpenmrsConstants.OPENMRS_VERSION_SHORT, new SearchQuery.Builder("description").withRequiredParameters(
		            "sourceName").build());
		when(searchHandler2.getSearchConfig()).thenReturn(searchConfig2);
		
		when(restHelperService.getRegisteredSearchHandlers()).thenReturn(Arrays.asList(searchHandler, searchHandler2));
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("code", new String[] { "some name" });
		
		SearchHandler searchHandler3 = restService.getSearchHandler("concept", parameters);
		assertThat(searchHandler3, nullValue());
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies return null if resource does not match
	 */
	@Test
	public void getSearchHandler_shouldReturnNullIfResourceDoesNotMatch() throws Exception {
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig("conceptByMapping", "concept", OpenmrsConstants.OPENMRS_VERSION_SHORT,
		        new SearchQuery.Builder("description").withRequiredParameters("sourceName").build());
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		
		when(restHelperService.getRegisteredSearchHandlers()).thenReturn(Arrays.asList(searchHandler));
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("sourceName", new String[] { "some name" });
		
		SearchHandler searchHandler2 = restService.getSearchHandler("nonexistingresource", parameters);
		assertThat(searchHandler2, nullValue());
	}
	
	@Test
	public void getResourceBySupportedClass_shouldReturnTheMostExactMatch() throws Exception {
		//Given
		RestServiceImpl service = new RestServiceImpl();
		
		Resource personResource = mock(Resource.class);
		Resource patientResource = mock(Resource.class);
		
		//Mocked for deterministic order
		service.resourceDefinitionsByNames = new LinkedHashMap<String, RestServiceImpl.ResourceDefinition>();
		service.resourcesBySupportedClasses = new LinkedHashMap<Class<?>, Resource>();
		
		service.resourcesBySupportedClasses.put(Person.class, personResource);
		service.resourcesBySupportedClasses.put(Patient.class, patientResource);
		
		//When
		Resource resource = service.getResourceBySupportedClass(ChildPatient.class);
		
		//Then
		assertThat(resource, is(patientResource));
	}
	
	public static class ChildPatient extends Patient {};
	
	/**
	 * @verifies return same hashcode for equal composite keys
	 * @see RestServiceImpl.CompositeSearchHandlerKey#hashCode()
	 */
	@Test
	public void hashCode_shouldReturnSameHashcodeForEqualCompositeKeys() throws Exception {
		Class compositeSearchHandlerClass = Class
		        .forName("org.openmrs.module.webservices.rest.web.api.impl.RestServiceImpl$CompositeSearchHandlerKey");
		Constructor ctor = compositeSearchHandlerClass.getDeclaredConstructors()[0];
		
		Object compositeKey1 = ctor.newInstance("v1/order", "default");
		Object compositeKey2 = ctor.newInstance("v1/order", "default");
		assertTrue(compositeKey1.equals(compositeKey2));
		
		assertThat(compositeKey1.hashCode(), is(compositeKey2.hashCode()));
	}
	
	/**
	 * @verifies return true if given this
	 * @see RestServiceImpl.CompositeSearchHandlerKey#equals(Object)
	 */
	@Test
	public void equals_shouldReturnTrueIfGivenThis() throws Exception {
		Class compositeSearchHandlerClass = Class
		        .forName("org.openmrs.module.webservices.rest.web.api.impl.RestServiceImpl$CompositeSearchHandlerKey");
		Constructor ctor = compositeSearchHandlerClass.getDeclaredConstructors()[0];
		
		Object compositeKey1 = ctor.newInstance("v1/order", "default");
		assertTrue(compositeKey1.equals(compositeKey1));
	}
	
	/**
	 * @verifies be symmetric
	 * @see RestServiceImpl.CompositeSearchHandlerKey#equals(Object)
	 */
	@Test
	public void equals_shouldBeSymmetric() throws Exception {
		Class compositeSearchHandlerClass = Class
		        .forName("org.openmrs.module.webservices.rest.web.api.impl.RestServiceImpl$CompositeSearchHandlerKey");
		Constructor ctor = compositeSearchHandlerClass.getDeclaredConstructors()[0];
		
		Object compositeKey1 = ctor.newInstance("v1/order", "default");
		Object compositeKey2 = ctor.newInstance("v1/order", "default");
		assertTrue(compositeKey1.equals(compositeKey2));
		assertTrue(compositeKey2.equals(compositeKey1));
	}
	
	/**
	 * @verifies be transitive
	 * @see RestServiceImpl.CompositeSearchHandlerKey#equals(Object)
	 */
	@Test
	public void equals_shouldBeTransitive() throws Exception {
		Class compositeSearchHandlerClass = Class
		        .forName("org.openmrs.module.webservices.rest.web.api.impl.RestServiceImpl$CompositeSearchHandlerKey");
		Constructor ctor = compositeSearchHandlerClass.getDeclaredConstructors()[0];
		
		Object compositeKey1 = ctor.newInstance("v1/order", "default");
		Object compositeKey2 = ctor.newInstance("v1/order", "default");
		Object compositeKey3 = ctor.newInstance("v1/order", "default");
		assertTrue(compositeKey1.equals(compositeKey2));
		assertTrue(compositeKey2.equals(compositeKey3));
		assertTrue(compositeKey1.equals(compositeKey3));
	}
	
	/**
	 * @verifies return false if given null
	 * @see RestServiceImpl.CompositeSearchHandlerKey#equals(Object)
	 */
	@Test
	public void equals_shouldReturnFalseIfGivenNull() throws Exception {
		Class compositeSearchHandlerClass = Class
		        .forName("org.openmrs.module.webservices.rest.web.api.impl.RestServiceImpl$CompositeSearchHandlerKey");
		Constructor ctor = compositeSearchHandlerClass.getDeclaredConstructors()[0];
		
		Object compositeKey1 = ctor.newInstance("v1/order", "default");
		assertFalse(compositeKey1.equals(null));
	}
	
	/**
	 * @verifies return false if given a composite key with different supported resource
	 * @see RestServiceImpl.CompositeSearchHandlerKey#equals(Object)
	 */
	@Test
	public void equals_shouldReturnFalseIfGivenACompositeKeyWithDifferentSupportedResource() throws Exception {
		Class compositeSearchHandlerClass = Class
		        .forName("org.openmrs.module.webservices.rest.web.api.impl.RestServiceImpl$CompositeSearchHandlerKey");
		Constructor ctor = compositeSearchHandlerClass.getDeclaredConstructors()[0];
		
		Object compositeKey1 = ctor.newInstance("v1/order", "default");
		Object compositeKey2 = ctor.newInstance("v2/order", "default");
		assertFalse(compositeKey1.equals(compositeKey2));
		assertFalse(compositeKey2.equals(compositeKey1));
	}
	
	/**
	 * @verifies return false if given a composite key with different additional key
	 * @see RestServiceImpl.CompositeSearchHandlerKey#equals(Object)
	 */
	@Test
	public void equals_shouldReturnFalseIfGivenACompositeKeyWithDifferentAdditionalKey() throws Exception {
		Class compositeSearchHandlerClass = Class
		        .forName("org.openmrs.module.webservices.rest.web.api.impl.RestServiceImpl$CompositeSearchHandlerKey");
		Constructor ctor = compositeSearchHandlerClass.getDeclaredConstructors()[0];
		
		Object compositeKey1 = ctor.newInstance("v1/order", "default");
		Object compositeKey2 = ctor.newInstance("v2/order", "custom");
		assertFalse(compositeKey1.equals(compositeKey2));
		assertFalse(compositeKey2.equals(compositeKey1));
	}
	
	/**
	 * @verifies return false if given an object which is not an instanceof this class
	 * @see RestServiceImpl.CompositeSearchHandlerKey#equals(Object)
	 */
	@Test
	public void equals_shouldReturnFalseIfGivenAnObjectWhichIsNotAnInstanceofThisClass() throws Exception {
		Class compositeSearchHandlerClass = Class
		        .forName("org.openmrs.module.webservices.rest.web.api.impl.RestServiceImpl$CompositeSearchHandlerKey");
		Constructor ctor = compositeSearchHandlerClass.getDeclaredConstructors()[0];
		
		Object compositeKey1 = ctor.newInstance("v1/order", "default");
		assertFalse(compositeKey1.equals("otherClass"));
	}
	
	/**
	 * @verifies return false if given a composite key with null as supported resource
	 * @see RestServiceImpl.CompositeSearchHandlerKey#equals(Object)
	 */
	@Test
	public void equals_shouldReturnFalseIfGivenACompositeKeyWithNullAsSupportedResource() throws Exception {
		Class compositeSearchHandlerClass = Class
		        .forName("org.openmrs.module.webservices.rest.web.api.impl.RestServiceImpl$CompositeSearchHandlerKey");
		Constructor ctor = compositeSearchHandlerClass.getDeclaredConstructors()[0];
		
		Object compositeKey1 = ctor.newInstance("v1/order", "default");
		Object compositeKey2 = ctor.newInstance("v2/order", null);
		assertFalse(compositeKey1.equals(compositeKey2));
		assertFalse(compositeKey2.equals(compositeKey1));
	}
	
	/**
	 * @verifies return false if given a composite key with null as additional key
	 * @see RestServiceImpl.CompositeSearchHandlerKey#equals(Object)
	 */
	@Test
	public void equals_shouldReturnFalseIfGivenACompositeKeyWithNullAsAdditionalKey() throws Exception {
		Class compositeSearchHandlerClass = Class
		        .forName("org.openmrs.module.webservices.rest.web.api.impl.RestServiceImpl$CompositeSearchHandlerKey");
		Constructor ctor = compositeSearchHandlerClass.getDeclaredConstructors()[0];
		
		Object compositeKey1 = ctor.newInstance("v1/order", "default");
		Object compositeKey2 = ctor.newInstance(null, "default");
		assertFalse(compositeKey1.equals(compositeKey2));
		assertFalse(compositeKey2.equals(compositeKey1));
	}
}
