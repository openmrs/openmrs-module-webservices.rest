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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.module.webservices.rest.web.api.impl.SchemaIntrospectionServiceImpl;

/**
 * Tests for schema introspection functionality.
 * This is a standard unit test rather than an integration test to avoid Spring context issues.
 */
public class SchemaIntrospectionTest {
	
	private SchemaIntrospectionServiceImpl service;
	
	@Before
	public void setup() {
		service = new SchemaIntrospectionServiceImpl();
	}
	
	/**
	 * Tests that reflection discovers expected properties on Patient
	 */
	@Test
	public void shouldDiscoverPatientProperties() {
		Map<String, String> properties = service.discoverAvailableProperties(Patient.class);
		
		// Verify discovered properties
		assertThat(properties, is(notNullValue()));
		assertThat(properties.size(), is(greaterThan(10))); // Should have many properties
		
		// Check some essential Patient properties
		assertThat(properties, hasKey("uuid"));
		assertThat(properties, hasKey("patientId"));
		assertThat(properties, hasKey("identifiers"));
		assertThat(properties, hasKey("person"));
		
		// Check some properties inherited from Person
		assertThat(properties, hasKey("gender"));
		assertThat(properties, hasKey("birthdate"));
		assertThat(properties, hasKey("names"));
		assertThat(properties, hasKey("addresses"));
	}
	
	/**
	 * Tests property discovery for another common class
	 */
	@Test
	public void shouldDiscoverPropertiesFromGetters() {
		// Use a simple class from the OpenMRS API
		Map<String, String> properties = service.discoverAvailableProperties(org.openmrs.Location.class);
		
		// Verify some expected properties
		assertThat(properties, is(notNullValue()));
		assertThat(properties.size(), is(greaterThan(5)));
		assertThat(properties, hasKey("uuid"));
		assertThat(properties, hasKey("name"));
		assertThat(properties, hasKey("description"));
		assertThat(properties, hasKey("address1")); // Fixed: Location uses address1, address2, etc.
		assertThat(properties, hasKey("address2")); // Added another address field to verify
		assertThat(properties, hasKey("parentLocation"));
		assertThat(properties, hasKey("childLocations"));
		assertThat(properties, hasKey("retired"));
	}
}