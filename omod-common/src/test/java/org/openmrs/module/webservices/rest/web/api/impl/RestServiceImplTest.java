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

import org.mockingbird.test.Cat;
import org.mockingbird.test.HibernateProxyMockingBird;
import org.mockingbird.test.MockingBird;
import org.mockingbird.test.rest.resource.AnimalResource_1_9;
import org.mockingbird.test.rest.resource.BirdResource_1_9;
import org.mockingbird.test.rest.resource.CatResource_1_9;
import org.mockingbird.test.rest.resource.DuplicateNameAndOrderMockingBirdResource_1_9;
import org.mockingbird.test.rest.resource.DuplicateNameMockingBirdResource_1_9;
import org.mockingbird.test.rest.resource.MockingBirdNameResource_1_9;
import org.mockingbird.test.rest.resource.MockingBirdResource_1_9;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockingbird.test.rest.resource.MockingBirdResource_1_11;
import org.mockingbird.test.rest.resource.UnannotatedMockingBirdResource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.APIException;
import org.openmrs.module.webservices.rest.web.OpenmrsClassScanner;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;
import org.openmrs.test.BaseContextMockTest;
import org.openmrs.util.OpenmrsConstants;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link RestServiceImpl}.
 */
public class RestServiceImplTest extends BaseContextMockTest {
	
	@Mock
	RestHelperService restHelperService;
	
	@Mock
	OpenmrsClassScanner openmrsClassScanner;
	
	@InjectMocks
	RestService restService = new RestServiceImpl();
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	/**
	 * @verifies return default representation if given null
	 * @see RestServiceImpl#getRepresentation(String)
	 */
	@Test
	public void getRepresentation_shouldReturnDefaultRepresentationIfGivenNull() throws Exception {
		
		assertThat(restService.getRepresentation(null), is(Representation.DEFAULT));
	}
	
	/**
	 * @verifies return default representation if given string is empty
	 * @see RestServiceImpl#getRepresentation(String)
	 */
	@Test
	public void getRepresentation_shouldReturnDefaultRepresentationIfGivenStringIsEmpty() throws Exception {
		
		assertThat(restService.getRepresentation(""), is(Representation.DEFAULT));
	}
	
	/**
	 * @verifies return reference representation if given string matches the ref representation
	 *           constant
	 * @see RestServiceImpl#getRepresentation(String)
	 */
	@Test
	public void getRepresentation_shouldReturnReferenceRepresentationIfGivenStringMatchesTheRefRepresentationConstant()
	        throws Exception {
		
		RestUtil.disableContext(); //to avoid a Context call
		assertThat(restService.getRepresentation("ref"), is(Representation.REF));
	}
	
	/**
	 * @verifies return default representation if given string matches the default representation
	 *           constant
	 * @see RestServiceImpl#getRepresentation(String)
	 */
	@Test
	public void getRepresentation_shouldReturnDefaultRepresentationIfGivenStringMatchesTheDefaultRepresentationConstant()
	        throws Exception {
		
		RestUtil.disableContext(); //to avoid a Context call
		assertThat(restService.getRepresentation("default"), is(Representation.DEFAULT));
	}
	
	/**
	 * @verifies return full representation if given string matches the full representation constant
	 * @see RestServiceImpl#getRepresentation(String)
	 */
	@Test
	public void getRepresentation_shouldReturnFullRepresentationIfGivenStringMatchesTheFullRepresentationConstant()
	        throws Exception {
		
		RestUtil.disableContext(); //to avoid a Context call
		assertThat(restService.getRepresentation("full"), is(Representation.FULL));
	}
	
	/**
	 * @verifies return an instance of custom representation if given string starts with the custom
	 *           representation prefix
	 * @see RestServiceImpl#getRepresentation(String)
	 */
	@Test
	public void getRepresentation_shouldReturnAnInstanceOfCustomRepresentationIfGivenStringStartsWithTheCustomRepresentationPrefix()
	        throws Exception {
		
		RestUtil.disableContext(); //to avoid a Context call
		Representation representation = restService.getRepresentation("custom:datatableslist");
		assertThat(representation, instanceOf(CustomRepresentation.class));
		assertThat(representation.getRepresentation(), is("datatableslist"));
	}
	
	/**
	 * @verifies return an instance of named representation for given string if it is not empty and
	 *           does not match any other case
	 * @see RestServiceImpl#getRepresentation(String)
	 */
	@Test
	public void getRepresentation_shouldReturnAnInstanceOfNamedRepresentationForGivenStringIfItIsNotEmptyAndDoesNotMatchAnyOtherCase()
	        throws Exception {
		
		RestUtil.disableContext(); //to avoid a Context call
		Representation representation = restService.getRepresentation("UNKNOWNREPRESENTATION");
		assertThat(representation, instanceOf(NamedRepresentation.class));
		assertThat(representation.getRepresentation(), is("UNKNOWNREPRESENTATION"));
	}
	
	/**
	 * @verifies return resource for given name
	 * @see RestServiceImpl#getResourceByName(String)
	 */
	@Test
	public void getResourceByName_shouldReturnResourceForGivenName() throws Exception {
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(MockingBirdResource_1_9.class);
		resources.add(MockingBirdResource_1_11.class);
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenReturn(resources);
		setCurrentOpenmrsVersion("1.9.10");
		
		assertThat(restService.getResourceByName("v1/mockingbird"), instanceOf(MockingBirdResource_1_9.class));
	}
	
	/**
	 * @verifies return resource for given name and ignore unannotated resources
	 * @see RestServiceImpl#getResourceByName(String)
	 */
	@Test
	public void getResourceByName_shouldReturnResourceForGivenNameAndIgnoreUnannotatedResources() throws Exception {
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(MockingBirdResource_1_9.class);
		resources.add(MockingBirdResource_1_11.class);
		resources.add(UnannotatedMockingBirdResource.class);
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenReturn(resources);
		setCurrentOpenmrsVersion("1.9.10");
		
		assertThat(restService.getResourceByName("v1/mockingbird"), instanceOf(MockingBirdResource_1_9.class));
	}
	
	/**
	 * Helper method to set the current OpenMRS version for tests.
	 * 
	 * @param currentOpenmrsVersion the openmrs version to set the current version to
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	private void setCurrentOpenmrsVersion(final String currentOpenmrsVersion) throws NoSuchFieldException,
	        IllegalAccessException {
		
		Field versionField = OpenmrsConstants.class.getDeclaredField("OPENMRS_VERSION_SHORT");
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(versionField, versionField.getModifiers() & ~Modifier.FINAL);
		versionField.set(null, currentOpenmrsVersion);
	}
	
	/**
	 * @verifies fail if failed to get resource classes
	 * @see RestServiceImpl#getResourceByName(String)
	 */
	@Test
	public void getResourceByName_shouldFailIfFailedToGetResourceClasses() throws Exception {
		
		IOException ioException = new IOException("some");
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenThrow(ioException);
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Cannot access REST resources");
		expectedException.expectCause(is(ioException));
		restService.getResourceByName("v1/mockingbird");
	}
	
	/**
	 * @verifies fail if resource for given name cannot be found
	 * @see RestServiceImpl#getResourceByName(String)
	 */
	@Test
	public void getResourceByName_shouldFailIfResourceForGivenNameCannotBeFound() throws Exception {
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Unknown resource: UNKNOWNRESOURCENAME");
		restService.getResourceByName("UNKNOWNRESOURCENAME");
	}
	
	/**
	 * @verifies fail if resource for given name does not support the current openmrs version
	 * @see RestServiceImpl#getResourceByName(String)
	 */
	@Test
	public void getResourceByName_shouldFailIfResourceForGivenNameDoesNotSupportTheCurrentOpenmrsVersion() throws Exception {
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(MockingBirdResource_1_9.class);
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenReturn(resources);
		setCurrentOpenmrsVersion("1.12.0");
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Unknown resource: v1/mockingbird");
		restService.getResourceByName("v1/mockingbird");
	}
	
	/**
	 * @verifies return subresource for given name
	 * @see RestServiceImpl#getResourceByName(String)
	 */
	@Test
	public void getResourceByName_shouldReturnSubresourceForGivenName() throws Exception {
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(MockingBirdResource_1_9.class);
		resources.add(MockingBirdNameResource_1_9.class);
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenReturn(resources);
		setCurrentOpenmrsVersion("1.9.10");
		
		assertThat(restService.getResourceByName("v1/mockingbird/name"), instanceOf(MockingBirdNameResource_1_9.class));
	}
	
	/**
	 * @verifies fail if subresource for given name does not support the current openmrs version
	 * @see RestServiceImpl#getResourceByName(String)
	 */
	@Test
	public void getResourceByName_shouldFailIfSubresourceForGivenNameDoesNotSupportTheCurrentOpenmrsVersion()
	        throws Exception {
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(MockingBirdResource_1_9.class);
		resources.add(MockingBirdNameResource_1_9.class);
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenReturn(resources);
		setCurrentOpenmrsVersion("1.12.0");
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Unknown resource: v1/mockingbird/name");
		restService.getResourceByName("v1/mockingbird/name");
	}
	
	/**
	 * @verifies fail if two resources with same name and order are found for given name
	 * @see RestServiceImpl#getResourceByName(String)
	 */
	@Test
	public void getResourceByName_shouldFailIfTwoResourcesWithSameNameAndOrderAreFoundForGivenName() throws Exception {
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(MockingBirdResource_1_9.class);
		resources.add(DuplicateNameAndOrderMockingBirdResource_1_9.class);
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenReturn(resources);
		setCurrentOpenmrsVersion("1.9.10");
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Two resources with the same name (v1/mockingbird) must not have the same order");
		restService.getResourceByName("v1/mockingbird");
	}
	
	/**
	 * @verifies return resource with lower order value if two resources with the same name are
	 *           found for given name
	 * @see RestServiceImpl#getResourceByName(String)
	 */
	@Test
	public void getResourceByName_shouldReturnResourceWithLowerOrderValueIfTwoResourcesWithTheSameNameAreFoundForGivenName()
	        throws Exception {
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(MockingBirdResource_1_9.class);
		resources.add(DuplicateNameMockingBirdResource_1_9.class);
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenReturn(resources);
		setCurrentOpenmrsVersion("1.9.10");
		
		assertThat(restService.getResourceByName("v1/mockingbird"), instanceOf(MockingBirdResource_1_9.class));
	}
	
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
	
	/**
	 * @verifies return resource supporting given class and current openmrs version
	 * @see RestServiceImpl#getResourceBySupportedClass(Class)
	 */
	@Test
	public void getResourceBySupportedClass_shouldReturnResourceSupportingGivenClassAndCurrentOpenmrsVersion()
	        throws Exception {
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(BirdResource_1_9.class);
		resources.add(MockingBirdResource_1_9.class);
		resources.add(MockingBirdResource_1_11.class);
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenReturn(resources);
		setCurrentOpenmrsVersion("1.9.10");
		
		assertThat(restService.getResourceBySupportedClass(MockingBird.class), instanceOf(MockingBirdResource_1_9.class));
	}
	
	/**
	 * @verifies fail if no resource supporting given class and current openmrs version was found
	 * @see RestServiceImpl#getResourceBySupportedClass(Class)
	 */
	@Test
	public void getResourceBySupportedClass_shouldFailIfNoResourceSupportingGivenClassAndCurrentOpenmrsVersionWasFound()
	        throws Exception {
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(BirdResource_1_9.class);
		resources.add(MockingBirdResource_1_9.class);
		resources.add(MockingBirdResource_1_11.class);
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenReturn(resources);
		setCurrentOpenmrsVersion("1.12.0");
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Unknown resource: class org.mockingbird.test.MockingBird");
		restService.getResourceBySupportedClass(MockingBird.class);
	}
	
	/**
	 * @verifies fail if no resource supporting given class was found
	 * @see RestServiceImpl#getResourceBySupportedClass(Class)
	 */
	@Test
	public void getResourceBySupportedClass_shouldFailIfNoResourceSupportingGivenClassWasFound() throws Exception {
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(BirdResource_1_9.class);
		resources.add(MockingBirdResource_1_9.class);
		resources.add(MockingBirdResource_1_11.class);
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenReturn(resources);
		setCurrentOpenmrsVersion("1.9.10");
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Unknown resource: class org.mockingbird.test.Cat");
		restService.getResourceBySupportedClass(Cat.class);
	}
	
	/**
	 * @verifies return resource supporting superclass of given class if given class is a hibernate
	 *           proxy
	 * @see RestServiceImpl#getResourceBySupportedClass(Class)
	 */
	@Test
	public void getResourceBySupportedClass_shouldReturnResourceSupportingSuperclassOfGivenClassIfGivenClassIsAHibernateProxy()
	        throws Exception {
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(MockingBirdResource_1_9.class);
		resources.add(MockingBirdResource_1_11.class);
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenReturn(resources);
		setCurrentOpenmrsVersion("1.9.10");
		
		assertThat(restService.getResourceBySupportedClass(HibernateProxyMockingBird.class),
		    instanceOf(MockingBirdResource_1_9.class));
	}
	
	/**
	 * @verifies return resource supporting superclass of given class if no resource supporting
	 *           given class was found
	 * @see RestServiceImpl#getResourceBySupportedClass(Class)
	 */
	@Test
	public void getResourceBySupportedClass_shouldReturnResourceSupportingSuperclassOfGivenClassIfNoResourceSupportingGivenClassWasFound()
	        throws Exception {
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(AnimalResource_1_9.class);
		resources.add(CatResource_1_9.class);
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenReturn(resources);
		setCurrentOpenmrsVersion("1.9.10");
		
		assertThat(restService.getResourceBySupportedClass(MockingBird.class), instanceOf(AnimalResource_1_9.class));
	}
	
	/**
	 * @verifies return resource supporting direct superclass of given class if no resource
	 *           supporting given class was found but multiple resources supporting multiple
	 *           superclasses exist
	 * @see RestServiceImpl#getResourceBySupportedClass(Class)
	 */
	@Test
	public void getResourceBySupportedClass_shouldReturnResourceSupportingDirectSuperclassOfGivenClassIfNoResourceSupportingGivenClassWasFoundButMultipleResourcesSupportingMultipleSuperclassesExist()
	        throws Exception {
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(AnimalResource_1_9.class);
		resources.add(BirdResource_1_9.class);
		resources.add(CatResource_1_9.class);
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenReturn(resources);
		setCurrentOpenmrsVersion("1.9.10");
		
		assertThat(restService.getResourceBySupportedClass(MockingBird.class), instanceOf(BirdResource_1_9.class));
	}
	
	/**
	 * @verifies fail if failed to get resource classes
	 * @see RestServiceImpl#getResourceBySupportedClass(Class)
	 */
	@Test
	public void getResourceBySupportedClass_shouldFailIfFailedToGetResourceClasses() throws Exception {
		
		IOException ioException = new IOException("some");
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenThrow(ioException);
		
		expectedException.expect(APIException.class);
		expectedException.expectMessage("Cannot access REST resources");
		expectedException.expectCause(is(ioException));
		restService.getResourceByName("v1/mockingbird");
	}
	
	/**
	 * @verifies fail if two resources with same name and order are found for given class
	 * @see RestServiceImpl#getResourceBySupportedClass(Class)
	 */
	@Test
	public void getResourceBySupportedClass_shouldFailIfTwoResourcesWithSameNameAndOrderAreFoundForGivenClass()
	        throws Exception {
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(MockingBirdResource_1_9.class);
		resources.add(DuplicateNameAndOrderMockingBirdResource_1_9.class);
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenReturn(resources);
		setCurrentOpenmrsVersion("1.9.10");
		
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("Two resources with the same name (v1/mockingbird) must not have the same order");
		restService.getResourceBySupportedClass(MockingBird.class);
	}
	
	/**
	 * @verifies return resource with lower order value if two resources with the same name are
	 *           found for given class
	 * @see RestServiceImpl#getResourceBySupportedClass(Class)
	 */
	@Test
	public void getResourceBySupportedClass_shouldReturnResourceWithLowerOrderValueIfTwoResourcesWithTheSameNameAreFoundForGivenClass()
	        throws Exception {
		
		List<Class<? extends Resource>> resources = new ArrayList<Class<? extends Resource>>();
		resources.add(MockingBirdResource_1_9.class);
		resources.add(DuplicateNameMockingBirdResource_1_9.class);
		
		when(openmrsClassScanner.getClasses(Resource.class, true)).thenReturn(resources);
		setCurrentOpenmrsVersion("1.9.10");
		
		assertThat(restService.getResourceBySupportedClass(MockingBird.class), instanceOf(MockingBirdResource_1_9.class));
	}
}
