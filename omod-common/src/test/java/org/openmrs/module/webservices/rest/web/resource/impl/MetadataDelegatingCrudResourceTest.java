/*
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

import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MetadataDelegatingCrudResourceTest {
	
	/**
	 * @verifies return a localized message if specified
	 */
	@Test
	public void getDisplayString_shouldReturnALocalizedMessageIfSpecified() throws Exception {
		String UUID = "0cbe2ed3-cd5f-4f46-9459-26127c9265ab";
		
		MessageSourceService messageSourceService = mock(MessageSourceService.class);
		when(messageSourceService.getMessage("ui.i18n.Location.name." + UUID)).thenReturn("Correct");
		ServiceContext.getInstance().setMessageSourceService(messageSourceService);
		
		Location location = new Location();
		location.setName("Incorrect");
		location.setUuid(UUID);
		
		MockLocationResource resource = new MockLocationResource();
		String display = resource.getDisplayString(location);
		
		assertThat(display, is("Correct"));
	}
	
	/**
	 * @verifies return the name property when no localized message is specified
	 */
	@Test
	public void getDisplayString_shouldReturnTheNamePropertyWhenNoLocalizedMessageIsSpecified() throws Exception {
		Location location = new Location();
		location.setName("Correct");
		
		MockLocationResource resource = new MockLocationResource();
		String display = resource.getDisplayString(location);
		
		assertThat(display, is("Correct"));
	}
	
	/**
	 * @verifies return the empty string when no localized message is specified and the name
	 *           property is null
	 */
	@Test
	public void getDisplayString_shouldReturnTheEmptyStringWhenNoLocalizedMessageIsSpecifiedAndTheNamePropertyIsNull()
	        throws Exception {
		Location location = new Location();
		location.setName(null);
		
		MockLocationResource resource = new MockLocationResource();
		String display = resource.getDisplayString(location);
		
		assertThat(display, is(""));
	}
	
	class MockLocationResource extends MetadataDelegatingCrudResource<Location> {
		
		@Override
		public Location getByUniqueId(String uniqueId) {
			return null;
		}
		
		@Override
		public Location newDelegate() {
			return new Location();
		}
		
		@Override
		public Location save(Location delegate) {
			return null;
		}
		
		@Override
		public void purge(Location delegate, RequestContext context) throws ResponseException {
		}
		
		@Override
		public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
			return null;
		}
	}
	
}
