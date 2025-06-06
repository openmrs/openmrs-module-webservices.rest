/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.RestTestConstants2_4;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.OrderResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_2.OrderResource2_2;
import org.openmrs.module.webservices.validation.ValidationException;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Integration tests for the framework that lets a resource handle an entire class hierarchy
 */
public class ClassHierarchyResourceTest extends BaseModuleWebContextSensitiveTest {
	
	private static final String DATASET_FILENAME = "customTestDataset.xml";
	
	private static final String PATIENT_UUID = "da7f524f-27ce-4bb2-86d6-6d1d05312bd5";

	private static final String ENCOUNTER_UUID = "c59b3942-4fdd-11e5-8c3c-410f8777c163";

	private static final String PROVIDER_UUID = "c2299800-dgha-11e0-9572-0800200c9a66";

	private static final String CARE_SETTING_UUID = "c365e560-c3ec-11e3-9c1a-0800200c9a66";
	
	private static final String SUPERCLASS_UUID = "ff97d3a0-8dbf-11e1-bc86-da3a922f3783";
	
	private static final String SUBCLASS_UUID = "921de0a3-05c4-444a-be03-e01b4c4b9142";
	
	private static final String ASPIRIN_CONCEPT_UUID = "15f83cd6-64e9-4e06-a5f9-364d3b14a43d";
	
	private static final String ASPIRIN_DRUG_UUID = "05ec820a-d297-44e3-be6e-698531d9dd3f";
	
	private static final String LUNCH_ORDER_TYPE_UUID = "e23733ab-787e-4096-8ba2-577a902d2c2b";
	
	RequestContext context;

	OrderResource2_2 resource;

	@Autowired
	@Qualifier("conceptService")
	ConceptService conceptService;
	
	@Before
	public void beforeEachTests() throws Exception {
		executeDataSet(DATASET_FILENAME);
		context = new RequestContext();
		resource = (OrderResource2_2) Context.getService(RestService.class).getResourceBySupportedClass(Order.class);
	}
	
	private SimpleObject buildSuperclass() {
		return new SimpleObject().add(RestConstants.PROPERTY_FOR_TYPE, "order").add("encounter", ENCOUNTER_UUID)
				.add("orderer", PROVIDER_UUID)
				.add("careSetting", CARE_SETTING_UUID)
		        .add("patient", PATIENT_UUID).add("concept", ASPIRIN_CONCEPT_UUID).add("orderType", "52a447d3-a64a-11e3-9aeb-50e549534c5e");
	}
	
	private SimpleObject buildSubclass() {
		return buildSuperclass().removeProperty("orderType").add(RestConstants.PROPERTY_FOR_TYPE, "drugorder")
		        .add("drug", ASPIRIN_DRUG_UUID).add("dose", "100").add("doseUnits", conceptService.getConcept(50))
				.add("frequency", "28090760-7c38-11e3-baa7-0800200c9a66").add("route", conceptService.getConcept(22))
				.add("numRefills", "0");
	}
	
	@Test
	public void shouldNotCreateASuperclass() throws Exception {
		try {
			resource.create(buildSuperclass(), context);
		} catch (Exception e) {
			// Must always be created with a subclass
			assertThat(e, is(instanceOf(ValidationException.class)));
			return;
		}
		fail();
	}
	
	@Test
	public void shouldCreateASubclass() throws Exception {
		SimpleObject created = (SimpleObject) resource.create(buildSubclass(), context);
		Util.log("Created subclass", created);
		Assert.assertEquals("drugorder", created.get("type"));
	}
	
	@Test
	public void shouldRetrieveASuperclass() throws Exception {
		SimpleObject retrieved = (SimpleObject) resource.retrieve(SUPERCLASS_UUID, context);
		Util.log("Retrieved superclass", retrieved);
		Assert.assertEquals("order", retrieved.get("type"));
		Assert.assertEquals("a09ab2c5-878e-4905-b25d-5784167d0216", Util.getByPath(retrieved, "concept/uuid"));
	}
	
	@Test
	public void shouldRetrieveASubclass() throws Exception {
		SimpleObject retrieved = (SimpleObject) resource.retrieve(SUBCLASS_UUID, context);
		Util.log("Retrieved subclass", retrieved);
		Assert.assertEquals("drugorder", retrieved.get("type"));
		Assert.assertEquals(325d, (double) retrieved.get("dose"), 0.0d);
	}
	
	@Test
	public void shouldUpdateASuperclass() throws Exception {
		try {
			String newValue = "Do a CD4 Test STAT!";
			resource.update(SUPERCLASS_UUID, new SimpleObject().add("instructions", newValue), context);
		} catch (Exception e) {
			assertThat(e, is(instanceOf(ResourceDoesNotSupportOperationException.class)));
			return;
		}
		fail();

	}
	
	@Test
	public void shouldUpdateASubclass() throws Exception {
		try {
			resource.update(SUBCLASS_UUID, new SimpleObject().add("dose", "500"), context);
		} catch (Exception e) {
			assertThat(e, is(instanceOf(ResourceDoesNotSupportOperationException.class)));
			return;
		}
		fail();

	}
	
	@Test
	public void shouldDeleteASuperclass() throws Exception {
		resource.delete(SUPERCLASS_UUID, "because", context);
		Order deleted = Context.getOrderService().getOrderByUuid(SUPERCLASS_UUID);
		Assert.assertTrue(deleted.isVoided());
	}
	
	@Test
	public void shouldDeleteASubclass() throws Exception {
		resource.delete(SUBCLASS_UUID, "because", context);
		Order deleted = Context.getOrderService().getOrderByUuid(SUBCLASS_UUID);
		Assert.assertTrue(deleted.isVoided());
	}
	
	@Test
	public void shouldPurgeASuperclass() throws Exception {
		resource.purge(SUPERCLASS_UUID, context);
		Order purged = Context.getOrderService().getOrderByUuid(SUPERCLASS_UUID);
		Assert.assertNull(purged);
	}
	
	@Test
	public void shouldPurgeASubclass() throws Exception {
		resource.purge("e1f95924-697a-11e3-bd76-0800271c1b75", context);
		Order purged = Context.getOrderService().getOrderByUuid("e1f95924-697a-11e3-bd76-0800271c1b75");
		Assert.assertNull(purged);
	}
	
	@Test
	public void shouldGetAll() throws Exception {
		try {
			resource.getAll(context);
		} catch (Exception e) {
			assertThat(e, is(instanceOf(ResourceDoesNotSupportOperationException.class)));
			return;
		}
		fail();
	}
	
	@Test
	public void shouldGetAllOfSubclass() throws Exception {
		try {
			context.setType("drugorder");
			resource.getAll(context);
		} catch (Exception e) {
			assertThat(e, is(instanceOf(ResourceDoesNotSupportOperationException.class)));
			return;
		}
		fail();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAllowSpecifyingDefaultTypeOnGetAll() throws Exception {
		context.setType("order");
		resource.getAll(context);
	}
	
	@Test
	public void shouldGetAllOrdersForAPatient() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("patient", PATIENT_UUID);
		context.setRequest(request);
		SimpleObject simple = resource.search(context);
		Util.log("all orders for patient", simple);
		Assert.assertEquals(5, Util.getResultsSize(simple));
		Object typeForFirst = Util.getByPath(simple, "results[0]/type");
		Assert.assertTrue("drugorder".equals(typeForFirst) || "order".equals(typeForFirst));
	}
	
	@Test
	public void shouldGetAllDrugOrdersForAPatient() throws Exception {
		context.setType("drugorder");
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("patient", PATIENT_UUID);
		context.setRequest(request);
		SimpleObject simple = resource.search(context);
		Util.log("drug orders for patient", simple);
		Assert.assertEquals(4, Util.getResultsSize(simple));
		Assert.assertEquals("drugorder", Util.getByPath(simple, "results[0]/type"));
	}
	
}
