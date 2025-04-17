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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

/**
 * Tests for {@link SchemaIntrospectionServiceImpl}.
 */
public class SchemaIntrospectionServiceImplTest {
	
	private SchemaIntrospectionServiceImpl service;
	
	@Before
	public void setup() {
		service = new SchemaIntrospectionServiceImpl();
	}
	
	/**
	 * @see SchemaIntrospectionServiceImpl#getDelegateType(Resource)
	 */
	@Test
	public void getDelegateType_shouldReturnNullForNullResource() {
		// Test with null resource
		assertThat(service.getDelegateType(null), is(nullValue()));
	}
	
	/**
	 * @see SchemaIntrospectionServiceImpl#getDelegateType(Resource)
	 */
	@Test
	public void getDelegateType_shouldReturnCorrectTypeForDelegatingCrudResource() {
		// Test with a mock DelegatingCrudResource
		TestPatientResource resource = new TestPatientResource();
		Class<?> delegateType = service.getDelegateType(resource);
		
		assertThat(delegateType, is(notNullValue()));
		assertThat(delegateType.getName(), is(Patient.class.getName()));
	}
	
	/**
	 * @see SchemaIntrospectionServiceImpl#getDelegateType(Resource)
	 */
	@Test
	public void getDelegateType_shouldReturnCorrectTypeForDelegatingSubResource() {
		// Test with a mock DelegatingSubResource
		TestPatientSubResource resource = new TestPatientSubResource();
		Class<?> delegateType = service.getDelegateType(resource);
		
		assertThat(delegateType, is(notNullValue()));
		assertThat(delegateType.getName(), is(Patient.class.getName()));
	}
	
	/**
	 * @see SchemaIntrospectionServiceImpl#discoverAvailableProperties(Class)
	 */
	@Test
	public void discoverAvailableProperties_shouldReturnEmptyMapForNullType() {
		Map<String, String> properties = service.discoverAvailableProperties(null);
		
		assertThat(properties, is(notNullValue()));
		assertThat(properties.size(), is(0));
	}
	
	/**
	 * @see SchemaIntrospectionServiceImpl#discoverAvailableProperties(Class)
	 */
	@Test
	public void discoverAvailableProperties_shouldIncludePropertiesFromGetters() {
		Map<String, String> properties = service.discoverAvailableProperties(Patient.class);
		
		// Test a few essential properties that should be discovered via getters
		assertThat(properties, hasKey("uuid"));
		assertThat(properties, hasKey("patientId"));
		assertThat(properties, hasKey("identifiers"));
		assertThat(properties, hasKey("person"));
		assertThat(properties, hasKey("voided"));
	}
	
	/**
	 * @see SchemaIntrospectionServiceImpl#discoverAvailableProperties(Class)
	 */
	@Test
	public void discoverAvailableProperties_shouldIncludePropertiesFromSuperclasses() {
		Map<String, String> properties = service.discoverAvailableProperties(Patient.class);
		
		// Patient extends Person, so should have Person properties
		assertThat(properties, hasKey("gender"));
		assertThat(properties, hasKey("age"));
		assertThat(properties, hasKey("birthdate"));
		assertThat(properties, hasKey("names"));
		assertThat(properties, hasKey("addresses"));
	}
	
	/**
	 * @see SchemaIntrospectionServiceImpl#discoverAvailableProperties(Class)
	 */
	@Test
	public void discoverAvailableProperties_shouldIncludePropertiesWithCorrectTypes() {
		Map<String, String> properties = service.discoverAvailableProperties(Patient.class);
		
		// Verify some property types
		assertThat(properties, hasEntry("uuid", "String"));
		assertThat(properties, hasEntry("voided", "Boolean"));
		// Set of PatientIdentifier
		assertThat(properties.get("identifiers").contains("Set"), is(true));
	}
	
	/**
	 * @see SchemaIntrospectionServiceImpl#discoverResourceProperties(Resource)
	 */
	@Test
	public void discoverResourceProperties_shouldCombineGetDelegateTypeAndDiscoverAvailableProperties() {
		TestPatientResource resource = new TestPatientResource();
		Map<String, String> properties = service.discoverResourceProperties(resource);
		
		// Verify it discovered Patient properties
		assertThat(properties, hasKey("uuid"));
		assertThat(properties, hasKey("patientId"));
		assertThat(properties, hasKey("identifiers"));
		
		// And Person properties (from superclass)
		assertThat(properties, hasKey("gender"));
		assertThat(properties, hasKey("birthdate"));
	}
	
	/**
	 * Mock DelegatingCrudResource for testing
	 */
	private class TestPatientResource extends DelegatingCrudResource<Patient> {
		
		@Override
		public Patient newDelegate() {
			return new Patient();
		}
		
		@Override
		public Patient save(Patient delegate) {
			return delegate;
		}
		
		@Override
		public Patient getByUniqueId(String uniqueId) {
			return new Patient();
		}
		
		@Override
		public void purge(Patient delegate, RequestContext context) {
		}
		
		@Override
		public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
			return null;
		}
		
		@Override
		public void delete(Patient delegate, String reason, RequestContext context) throws ResourceDoesNotSupportOperationException {
			throw new ResourceDoesNotSupportOperationException();
		}
	}
	
	/**
	 * Mock DelegatingSubResource for testing
	 */
	private class TestPatientSubResource extends DelegatingSubResource<Patient, Person, TestPersonResource> {
		
		@Override
		public Patient getByUniqueId(String uniqueId) {
			return new Patient();
		}
		
		@Override
		public Patient newDelegate() {
			return new Patient();
		}
		
		@Override
		public Patient save(Patient delegate) {
			return delegate;
		}
		
		@Override
		public void purge(Patient delegate, RequestContext context) {
		}
		
		@Override
		public Person getParent(Patient instance) {
			return instance.getPerson();
		}
		
		@Override
		public void setParent(Patient instance, Person parent) {
			// In actual Patient class, the setPerson method is generated by Hibernate
			// For our test, we'll just ignore the call
		}
		
		@Override
		public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
			return null;
		}
		
		@Override
		public org.openmrs.module.webservices.rest.web.resource.api.PageableResult doGetAll(
		        Person parent,
		        RequestContext context) {
			return null;
		}
		
		@Override
		public void delete(Patient delegate, String reason, RequestContext context) throws ResourceDoesNotSupportOperationException {
			throw new ResourceDoesNotSupportOperationException();
		}
	}
	
	/**
	 * Mock TestPersonResource for testing sub-resources
	 */
	private class TestPersonResource extends DelegatingCrudResource<Person> {
		
		@Override
		public Person newDelegate() {
			return new Person();
		}
		
		@Override
		public Person save(Person delegate) {
			return delegate;
		}
		
		@Override
		public Person getByUniqueId(String uniqueId) {
			return new Person();
		}
		
		@Override
		public void purge(Person delegate, RequestContext context) {
		}
		
		@Override
		public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
			return null;
		}
		
		@Override
		public void delete(Person delegate, String reason, RequestContext context) throws ResourceDoesNotSupportOperationException {
			throw new ResourceDoesNotSupportOperationException();
		}
	}
}