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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_10;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.CareSetting;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_10;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Integration tests for the Order resource
 */
public class OrderController1_10Test extends MainResourceControllerTest {
	
	private final static String PATIENT_UUID = "5946f880-b197-400b-9caa-a3c661d23041";
	
	private final static String OUTPATIENT_CARE_SETTING_UUID = "2ed1e57d-9f18-41d3-b067-2eeaf4b30fb1";
	
	private OrderService orderService;
	
	private PatientService patientService;
	
	@Before
	public void before() throws Exception {
		this.orderService = Context.getOrderService();
		this.patientService = Context.getPatientService();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "order";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_10.ORDER_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		//TODO Not yet supported should be though after reworking OrderService.getOrders
		//See https://tickets.openmrs.org/browse/TRUNK-4173
		return 0;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#shouldGetAll()
	 */
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
	
	@Test
	public void shouldGetOrderByUuid() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI() + "/" + getUuid())));
		assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
	}
	
	@Test
	public void shouldGetOrderByOrderNumber() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI() + "/ORD-7")));
		assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
	}
	
	@Test
	public void shouldPlaceANewOrder() throws Exception {
		CareSetting outPatient = orderService.getCareSettingByUuid(OUTPATIENT_CARE_SETTING_UUID);
		Patient patient = patientService.getPatientByUuid(PATIENT_UUID);
		int originalActiveOrderCount = orderService.getActiveOrders(patient, null, outPatient, null).size();
		
		SimpleObject order = new SimpleObject();
		order.add("type", "order");
		order.add("patient", PATIENT_UUID);
		order.add("concept", "a09ab2c5-878e-4905-b25d-5784167d0216");
		order.add("action", Order.Action.NEW.toString());
		order.add("careSetting", OUTPATIENT_CARE_SETTING_UUID);
		order.add("startDate", "2008-08-19");
		order.add("encounter", "e403fafb-e5e4-42d0-9d11-4f52e89d148c");
		
		MockHttpServletRequest req = newPostRequest(getURI(), order);
		SimpleObject newOrder = deserialize(handle(req));
		
		assertNotNull(PropertyUtils.getProperty(newOrder, "orderNumber"));
		List<Order> activeOrders = orderService.getActiveOrders(patient, null, outPatient, null);
		assertEquals(++originalActiveOrderCount, activeOrders.size());
	}
}
