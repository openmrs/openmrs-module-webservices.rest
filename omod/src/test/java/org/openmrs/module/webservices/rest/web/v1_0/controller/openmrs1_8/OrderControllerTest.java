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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Integration tests for the Order resource
 */
public class OrderControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	private final static String ORDER_UUID = "ff97d3a0-8dbf-11e1-bc86-da3a922f3783";
	//	
	//	private final static String DRUG_ORDER_UUID = "921de0a3-05c4-444a-be03-e01b4c4b9142";
	//	
	//	private final static String PATIENT_UUID = "5946f880-b197-400b-9caa-a3c661d23041";
	//	
	//	private OrderService service;
	//	
	//	private OrderController controller;
	//	
	//	private MockHttpServletRequest request;
	//	
	//	private HttpServletResponse response;
	//	
	//	@Before
	//	public void before() throws Exception {
	//		executeDataSet("customTestDataset.xml");
	//		this.service = Context.getOrderService();
	//		this.controller = new OrderController();
	//		this.request = new MockHttpServletRequest();
	//		this.response = new MockHttpServletResponse();
	//	}
	//	
	//	@Test
	//	public void shouldGetOrderAsRef() throws Exception {
	//		request.setParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, Representation.REF.getRepresentation());
	//		Object o = controller.retrieve(ORDER_UUID, request);
	//		Assert.assertEquals(ORDER_UUID, PropertyUtils.getProperty(o, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(o, "display"));
	//		Assert.assertNull(PropertyUtils.getProperty(o, "concept"));
	//		Util.log("order as ref", o);
	//	}
	//	
	//	@Test
	//	public void shouldGetOrderAsDefault() throws Exception {
	//		Object o = controller.retrieve(ORDER_UUID, request);
	//		Assert.assertEquals(ORDER_UUID, PropertyUtils.getProperty(o, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(o, "display"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(o, "patient"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(o, "concept"));
	//		Util.log("drug order as ref", o);
	//	}
	//	
	//	@Test
	//	public void shouldGetDrugOrderAsRef() throws Exception {
	//		request.setParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, Representation.REF.getRepresentation());
	//		Object o = controller.retrieve(DRUG_ORDER_UUID, request);
	//		Assert.assertEquals(DRUG_ORDER_UUID, PropertyUtils.getProperty(o, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(o, "display"));
	//		Assert.assertNull(PropertyUtils.getProperty(o, "concept"));
	//	}
	//	
	//	@Test
	//	public void shouldGetDrugOrderAsDefault() throws Exception {
	//		Object o = controller.retrieve(DRUG_ORDER_UUID, request);
	//		Assert.assertEquals(DRUG_ORDER_UUID, PropertyUtils.getProperty(o, "uuid"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(o, "display"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(o, "patient"));
	//		Assert.assertNotNull(PropertyUtils.getProperty(o, "concept"));
	//	}
	//	
	//	@Test
	//	public void shouldGetAllOrders() throws Exception {
	//		SimpleObject all = controller.getAll(request, response);
	//		Assert.assertEquals(6, Util.getResultsSize(all));
	//		// 5 should be drugorders, one should be a plain order
	//		int numDrugOrders = 0;
	//		int numOrders = 0;
	//		for (Object o : Util.getResultsList(all)) {
	//			if ("drugorder".equals(PropertyUtils.getProperty(o, "type")))
	//				numDrugOrders += 1;
	//			else if ("order".equals(PropertyUtils.getProperty(o, "type")))
	//				numOrders += 1;
	//		}
	//		Assert.assertEquals(5, numDrugOrders);
	//		Assert.assertEquals(1, numOrders);
	//	}
	//	
	//	@Test
	//	public void shouldGetAllDrugOrders() throws Exception {
	//		request.setParameter(RestConstants.REQUEST_PROPERTY_FOR_TYPE, "drugorder");
	//		SimpleObject all = controller.getAll(request, response);
	//		Assert.assertEquals(5, Util.getResultsSize(all));
	//		for (Object o : Util.getResultsList(all))
	//			Assert.assertEquals("drugorder", PropertyUtils.getProperty(o, "type"));
	//	}
	//	
	//	@Test
	//	public void shouldGetAllOrdersByPatient() throws Exception {
	//		SimpleObject all = controller.searchByPatient(PATIENT_UUID, request, response);
	//		Assert.assertEquals(2, Util.getResultsSize(all));
	//		// 1 should be drugorder, 1 should be a plain order
	//		int numDrugOrders = 0;
	//		int numOrders = 0;
	//		for (Object o : Util.getResultsList(all)) {
	//			if ("drugorder".equals(PropertyUtils.getProperty(o, "type")))
	//				numDrugOrders += 1;
	//			else if ("order".equals(PropertyUtils.getProperty(o, "type")))
	//				numOrders += 1;
	//		}
	//		Assert.assertEquals(1, numDrugOrders);
	//		Assert.assertEquals(1, numOrders);
	//	}
	//	
	//	@Test
	//	public void shouldGetAllDrugOrdersByPatient() throws Exception {
	//		request.setParameter(RestConstants.REQUEST_PROPERTY_FOR_TYPE, "drugorder");
	//		SimpleObject all = controller.searchByPatient(PATIENT_UUID, request, response);
	//		Assert.assertEquals(1, Util.getResultsSize(all));
	//		Assert.assertEquals("drugorder", Util.getByPath(all, "results[0]/type"));
	//	}
	//	
	//	@Test
	//	public void shouldCreateOrder() throws Exception {
	//		String conceptUuid = "0dde1358-7fcf-4341-a330-f119241a46e8";
	//		String orderTypeUuid = "e23733ab-787e-4096-8ba2-577a902d2c2b";
	//		SimpleObject post = new SimpleObject().add("type", "order").add("patient", PATIENT_UUID).add("concept", conceptUuid)
	//		        .add("orderType", orderTypeUuid);
	//		Object o = controller.create(post, request, response);
	//		Assert.assertEquals("order", PropertyUtils.getProperty(o, "type"));
	//		Assert.assertEquals(conceptUuid, Util.getByPath(o, "concept/uuid"));
	//		Assert.assertEquals(orderTypeUuid, Util.getByPath(o, "orderType/uuid"));
	//		Assert.assertEquals(PATIENT_UUID, Util.getByPath(o, "patient/uuid"));
	//	}
	//	
	//	@Test
	//	public void shouldCreateDrugOrder() throws Exception {
	//		String conceptUuid = "d144d24f-6913-4b63-9660-a9108c2bebef";
	//		String drugUuid = "3cfcf118-931c-46f7-8ff6-7b876f0d4202";
	//		SimpleObject post = new SimpleObject().add("type", "drugorder").add("patient", PATIENT_UUID).add("concept",
	//		    conceptUuid).add("dose", "1").add("units", "tablet").add("drug", drugUuid);
	//		Object o = controller.create(post, request, response);
	//		Assert.assertEquals("drugorder", PropertyUtils.getProperty(o, "type"));
	//		Assert.assertEquals(conceptUuid, Util.getByPath(o, "concept/uuid"));
	//		Assert.assertEquals(PATIENT_UUID, Util.getByPath(o, "patient/uuid"));
	//		Assert.assertEquals("tablet", PropertyUtils.getProperty(o, "units"));
	//		Assert.assertEquals(drugUuid, Util.getByPath(o, "drug/uuid"));
	//	}
	//	
	//	@Test
	//	public void shouldUpdateOrder() throws Exception {
	//		String newInstructions = "STAT!";
	//		controller.update(ORDER_UUID, new SimpleObject().add("instructions", newInstructions), request, response);
	//		Assert.assertEquals(newInstructions, service.getOrderByUuid(ORDER_UUID).getInstructions());
	//	}
	//	
	//	@Test
	//	public void shouldUpdateDrugOrder() throws Exception {
	//		controller.update(DRUG_ORDER_UUID, new SimpleObject().add("dose", "500"), request, response);
	//		Assert.assertEquals(500d, ((DrugOrder) service.getOrderByUuid(DRUG_ORDER_UUID)).getDose());
	//	}
	//	
	//	@Test(expected = IllegalArgumentException.class)
	//	public void shouldFailToChangeOrderClass() throws Exception {
	//		controller.update(ORDER_UUID, new SimpleObject().add("type", "drugorder").add("instructions", "This will fail"),
	//		    request, response);
	//	}
	//	
	//	@Test
	//	public void shouldDeleteOrder() throws Exception {
	//		Assert.assertFalse(service.getOrderByUuid(ORDER_UUID).isVoided());
	//		controller.delete(ORDER_UUID, "because", request, response);
	//		Assert.assertTrue(service.getOrderByUuid(ORDER_UUID).isVoided());
	//	}
	//	
	//	@Test
	//	public void shouldDeleteDrugOrder() throws Exception {
	//		Assert.assertFalse(service.getOrderByUuid(DRUG_ORDER_UUID).isVoided());
	//		controller.delete(DRUG_ORDER_UUID, "because", request, response);
	//		Assert.assertTrue(service.getOrderByUuid(DRUG_ORDER_UUID).isVoided());
	//	}
	//	
	//	@Test
	//	public void shouldPurgeOrder() throws Exception {
	//		Assert.assertNotNull(service.getOrderByUuid(ORDER_UUID));
	//		controller.purge(ORDER_UUID, request, response);
	//		Assert.assertNull(service.getOrderByUuid(ORDER_UUID));
	//	}
	//	
	//	@Test
	//	public void shouldPurgeDrugOrder() throws Exception {
	//		Assert.assertNotNull(service.getOrderByUuid(DRUG_ORDER_UUID));
	//		controller.purge(DRUG_ORDER_UUID, request, response);
	//		Assert.assertNull(service.getOrderByUuid(DRUG_ORDER_UUID));
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
