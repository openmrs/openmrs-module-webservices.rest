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

import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import org.mockito.Matchers;

import org.openmrs.ConceptNumeric;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.api.AdministrationService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.resource.api.CrudResource;
import org.openmrs.module.webservices.rest.web.resource.api.Listable;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainSubResourceController;
import org.openmrs.module.webservices.validation.ValidateUtil;

public class DelegatingCrudResourceTest {
	
	@Test
	public void testRetrieve() {
		SimpleObject sob = new SimpleObject();
		DelegatingCrudResource dcr = mock(DelegatingCrudResource.class);
		when(dcr.getByUniqueId(anyString())).thenReturn("Foo");
		when(dcr.asRepresentation(anyObject(), any(Representation.class))).thenReturn(sob);
		when(dcr.hasTypesDefined()).thenReturn(false);
		when(dcr.retrieve(anyString(), any(RequestContext.class))).thenCallRealMethod();
		
		ServiceContext scon = ServiceContext.getInstance();
		AdministrationService adm = mock(AdministrationService.class);
		scon.setAdministrationService(adm);
		
		RequestContext req = new RequestContext();
		Object retrieved = dcr.retrieve("foo", req);
		Assert.assertEquals(sob, retrieved);
	}
	
	@Test
	public void testCreate() {
		DelegatingCrudResource dcr = mock(DelegatingCrudResource.class);
		when(dcr.hasTypesDefined()).thenReturn(false);
		when(dcr.newDelegate(any(SimpleObject.class))).thenReturn(new ConceptNumeric());
		when(dcr.create(any(SimpleObject.class), any(RequestContext.class))).thenCallRealMethod();
		
		ServiceContext scon = ServiceContext.getInstance();
		AdministrationService adm = mock(AdministrationService.class);
		scon.setAdministrationService(adm);
		
		dcr.create(new SimpleObject(), new RequestContext());
	}
	
	@Test
	public void testUpdate() {
		DelegatingCrudResource dcr = mock(DelegatingCrudResource.class);
		when(dcr.hasTypesDefined()).thenReturn(false);
		when(dcr.update(anyString(), any(SimpleObject.class), any(RequestContext.class))).thenCallRealMethod();
		when(dcr.getByUniqueId(anyString())).thenReturn(new ConceptNumeric());
		when(dcr.getResourceHandler(anyObject())).thenReturn(dcr);
		
		dcr.update("foo", new SimpleObject(), new RequestContext());
		
	}
	
	@Test
	public void testGetAll() {
		DelegatingCrudResource dcr = mock(DelegatingCrudResource.class);
		DelegatingSubclassHandler dsh = mock(DelegatingSubclassHandler.class);
		when(dcr.getAll(any(RequestContext.class))).thenCallRealMethod();
		when(dcr.hasTypesDefined()).thenReturn(true);
		when(dcr.getSubclassHandler(anyString())).thenReturn(dsh);
		
		when(dsh.getAllByType(any(RequestContext.class))).thenReturn(new EmptySearchResult());
		
		RequestContext rcon = new RequestContext();
		rcon.setType("foo");
		dcr.getAll(rcon);
	}
}
