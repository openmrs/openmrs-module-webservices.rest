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
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_10;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Integration tests for the Order resource
 */
public class OrderController1_10Test extends MainResourceControllerTest {
	
	protected static final String ORDER_ENTRY_DATASET_XML = "org/openmrs/api/include/OrderEntryIntegrationTest-other.xml";
	
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
		order.add("action", "NEW");
		order.add("careSetting", OUTPATIENT_CARE_SETTING_UUID);
		order.add("startDate", "2008-08-19");
		order.add("encounter", "e403fafb-e5e4-42d0-9d11-4f52e89d148c");
		order.add("orderer", "c2299800-cca9-11e0-9572-0800200c9a66");
		
		MockHttpServletRequest req = newPostRequest(getURI(), order);
		SimpleObject newOrder = deserialize(handle(req));
		
		List<Order> activeOrders = orderService.getActiveOrders(patient, null, outPatient, null);
		assertEquals(++originalActiveOrderCount, activeOrders.size());
		
		assertNotNull(PropertyUtils.getProperty(newOrder, "orderNumber"));
		assertEquals(order.get("action"), Util.getByPath(newOrder, "action"));
		assertEquals(order.get("patient"), Util.getByPath(newOrder, "patient/uuid"));
		assertEquals(order.get("concept"), Util.getByPath(newOrder, "concept/uuid"));
		assertEquals(order.get("careSetting"), Util.getByPath(newOrder, "careSetting/uuid"));
		assertNotNull(PropertyUtils.getProperty(newOrder, "startDate"));
		assertEquals(order.get("encounter"), Util.getByPath(newOrder, "encounter/uuid"));
		assertEquals(order.get("orderer"), Util.getByPath(newOrder, "orderer/uuid"));
	}
	
	@Test
	public void shouldPlaceANewDrugOrder() throws Exception {
		executeDataSet(ORDER_ENTRY_DATASET_XML);
		CareSetting outPatient = orderService.getCareSettingByUuid(OUTPATIENT_CARE_SETTING_UUID);
		Patient patient = patientService.getPatientByUuid(PATIENT_UUID);
		int originalActiveDrugOrderCount = orderService.getActiveOrders(patient, DrugOrder.class, outPatient, null).size();
		SimpleObject order = new SimpleObject();
		order.add("type", "drugorder");
		order.add("patient", PATIENT_UUID);
		order.add("concept", "15f83cd6-64e9-4e06-a5f9-364d3b14a43d");
		order.add("action", "NEW");
		order.add("careSetting", OUTPATIENT_CARE_SETTING_UUID);
		order.add("startDate", "2008-08-19");
		order.add("encounter", "e403fafb-e5e4-42d0-9d11-4f52e89d148c");
		order.add("drug", "05ec820a-d297-44e3-be6e-698531d9dd3f");
		order.add("orderer", "c2299800-cca9-11e0-9572-0800200c9a66");
		order.add("dosingType", "SIMPLE");
		order.add("dose", "300.0");
		order.add("doseUnits", "557b9699-68a3-11e3-bd76-0800271c1b75");
		order.add("quantity", "20.0");
		order.add("quantityUnits", "5a2aa3db-68a3-11e3-bd76-0800271c1b75");
		order.add("duration", "20.0");
		order.add("durationUnits", "7e02d1a0-7869-11e4-981f-0800200c9a75");
		order.add("frequency", "38090760-7c38-11e4-baa7-0800200c9a67");
		
		MockHttpServletRequest req = newPostRequest(getURI(), order);
		SimpleObject newOrder = deserialize(handle(req));
		
		List<DrugOrder> activeDrugOrders = orderService.getActiveOrders(patient, DrugOrder.class, outPatient, null);
		assertEquals(++originalActiveDrugOrderCount, activeDrugOrders.size());
		
		assertNotNull(PropertyUtils.getProperty(newOrder, "orderNumber"));
		assertEquals(order.get("action"), Util.getByPath(newOrder, "action"));
		assertEquals(order.get("patient"), Util.getByPath(newOrder, "patient/uuid"));
		assertEquals(order.get("concept"), Util.getByPath(newOrder, "concept/uuid"));
		assertEquals(order.get("careSetting"), Util.getByPath(newOrder, "careSetting/uuid"));
		assertNotNull(PropertyUtils.getProperty(newOrder, "startDate"));
		assertEquals(order.get("encounter"), Util.getByPath(newOrder, "encounter/uuid"));
		assertEquals(order.get("orderer"), Util.getByPath(newOrder, "orderer/uuid"));
		assertEquals(order.get("drug"), Util.getByPath(newOrder, "drug/uuid"));
		assertEquals(order.get("dosingType"), Util.getByPath(newOrder, "dosingType"));
		assertEquals(order.get("dose"), Util.getByPath(newOrder, "dose").toString());
		assertEquals(order.get("doseUnits"), Util.getByPath(newOrder, "doseUnits/uuid"));
		assertEquals(order.get("quantity"), Util.getByPath(newOrder, "quantity").toString());
		assertEquals(order.get("quantityUnits"), Util.getByPath(newOrder, "quantityUnits/uuid"));
		assertEquals(order.get("duration"), Util.getByPath(newOrder, "duration").toString());
		assertEquals(order.get("durationUnits"), Util.getByPath(newOrder, "durationUnits/uuid"));
		assertEquals(order.get("frequency"), Util.getByPath(newOrder, "frequency/uuid"));
	}
}
