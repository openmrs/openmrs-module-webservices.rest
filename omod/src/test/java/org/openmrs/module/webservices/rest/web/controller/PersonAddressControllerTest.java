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
package org.openmrs.module.webservices.rest.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Tests functionality of {@link PersonAddressController.
 */
public class PersonAddressControllerTest extends BaseModuleWebContextSensitiveTest {
	
	String personUuid = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";
	
	private PersonService service;
	
	private PersonAddressController controller;
	
	private WebRequest request;
	
	private HttpServletResponse response;
	
	@Before
	public void before() {
		this.service = Context.getPersonService();
		this.controller = new PersonAddressController();
		this.request = new ServletWebRequest(new MockHttpServletRequest());
		this.response = new MockHttpServletResponse();
	}
	
	@Test
	public void shouldGetAnAddressForAPerson() throws Exception {
		Object result = controller.retrieve(personUuid, "3350d0b5-821c-4e5e-ad1d-a9bce331e118", request);
		Assert.assertNotNull(result);
		Assert.assertEquals("3350d0b5-821c-4e5e-ad1d-a9bce331e118", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals("1050 Wishard Blvd.", PropertyUtils.getProperty(result, "address1"));
		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	@Test
	public void shouldGetAllNonVoidedAddressesForAPerson() throws Exception {
		List<Object> result = controller.getAll(personUuid, request, response);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
	}
	
	@Test
	public void shouldExcludeVoidedAddressesForAPerson() throws Exception {
		//For test purposes, we need to void the current address
		Person p = service.getPersonByUuid(personUuid);
		PersonAddress ad = p.getAddresses().iterator().next();
		ad.setVoided(true);
		service.savePerson(p);
		List<Object> result = controller.getAll(personUuid, request, response);
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}
	
	@Test
	public void shouldAddAnAddressToAPerson() throws Exception {
		int before = service.getPersonByUuid(personUuid).getAddresses().size();
		String json = "{ \"address1\":\"test address\", \"country\":\"USA\", \"preferred\":true }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.create(personUuid, post, request, response);
		int after = service.getPersonByUuid(personUuid).getAddresses().size();
		Assert.assertEquals(before + 1, after);
	}
	
	@Test
	public void shouldEditIAnAddress() throws Exception {
		String json = "{ \"address1\":\"new address1\", \"address2\":\"new address2\"  }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.update(personUuid, "3350d0b5-821c-4e5e-ad1d-a9bce331e118", post, request, response);
		PersonAddress updated = service.getPersonAddressByUuid("3350d0b5-821c-4e5e-ad1d-a9bce331e118");
		Assert.assertNotNull(updated);
		Assert.assertEquals("new address1", updated.getAddress1());
		Assert.assertEquals("new address2", updated.getAddress2());
	}
	
	@Test
	public void shouldVoidADeletedPersonAddress() throws Exception {
		String addressUuid = "3350d0b5-821c-4e5e-ad1d-a9bce331e118";
		PersonAddress address = service.getPersonAddressByUuid(addressUuid);
		Assert.assertFalse(address.isVoided());
		controller.delete(personUuid, addressUuid, "test reason", request, response);
		
		address = service.getPersonAddressByUuid(addressUuid);
		Assert.assertTrue(address.isVoided());
		Assert.assertEquals("test reason", address.getVoidReason());
	}
	
	@Test
	public void shouldPurgeAPersonAddress() throws Exception {
		String addressUuid = "3350d0b5-821c-4e5e-ad1d-a9bce331e118";
		PersonAddress address = service.getPersonAddressByUuid(addressUuid);
		Assert.assertNotNull(address);
		int before = service.getPersonByUuid(personUuid).getAddresses().size();
		controller.purge(personUuid, addressUuid, request, response);
		
		address = service.getPersonAddressByUuid(addressUuid);
		Assert.assertNull(address);
		Assert.assertEquals(before - 1, service.getPersonByUuid(personUuid).getAddresses().size());
	}
	
}
