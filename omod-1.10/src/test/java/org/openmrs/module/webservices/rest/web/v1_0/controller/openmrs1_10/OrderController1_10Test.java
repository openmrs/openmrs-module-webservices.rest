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

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openmrs.module.webservices.rest.test.SameDatetimeMatcher.sameDatetime;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.CareSetting;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.TestOrder;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_10;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10.DrugOrderSubclassHandler1_10;
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
		assertThat(Util.getByPath(newOrder, "startDate").toString(), sameDatetime(order.get("startDate").toString()));
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
		assertThat(Util.getByPath(newOrder, "startDate").toString(), sameDatetime(order.get("startDate").toString()));
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
	
	@Test
	public void shouldPlaceANewTestOrder() throws Exception {
		executeDataSet(ORDER_ENTRY_DATASET_XML);
		CareSetting outPatient = orderService.getCareSettingByUuid(OUTPATIENT_CARE_SETTING_UUID);
		Patient patient = patientService.getPatientByUuid(PATIENT_UUID);
		int originalActiveTestOrderCount = orderService.getActiveOrders(patient, TestOrder.class, outPatient, null).size();
		
		SimpleObject order = new SimpleObject();
		order.add("type", "testorder");
		order.add("patient", PATIENT_UUID);
		final String cd4CountUuid = "a09ab2c5-878e-4905-b25d-5784167d0216";
		order.add("concept", cd4CountUuid);
		order.add("action", "NEW");
		order.add("careSetting", OUTPATIENT_CARE_SETTING_UUID);
		order.add("startDate", "2008-08-19");
		order.add("encounter", "e403fafb-e5e4-42d0-9d11-4f52e89d148c");
		order.add("orderer", "c2299800-cca9-11e0-9572-0800200c9a66");
		order.add("clinicalHistory", "Patient had a negative reaction to the test in the past");
		String onceUuid = "38090760-7c38-11e4-baa7-0800200c9a67";
		order.add("frequency", onceUuid);
		String bloodUuid = "857eba27-2b38-43e8-91a9-4dfe3956a32e";
		order.add("specimenSource", bloodUuid);
		order.add("numberOfRepeats", "3");
		
		MockHttpServletRequest req = newPostRequest(getURI(), order);
		SimpleObject newOrder = deserialize(handle(req));
		
		List<TestOrder> activeTestOrders = orderService.getActiveOrders(patient, TestOrder.class, outPatient, null);
		assertEquals(++originalActiveTestOrderCount, activeTestOrders.size());
		
		assertNotNull(PropertyUtils.getProperty(newOrder, "orderNumber"));
		assertEquals(order.get("action"), Util.getByPath(newOrder, "action"));
		assertEquals(order.get("patient"), Util.getByPath(newOrder, "patient/uuid"));
		assertEquals(order.get("concept"), Util.getByPath(newOrder, "concept/uuid"));
		assertEquals(order.get("careSetting"), Util.getByPath(newOrder, "careSetting/uuid"));
		assertThat(Util.getByPath(newOrder, "startDate").toString(), sameDatetime(order.get("startDate").toString()));
		assertEquals(order.get("encounter"), Util.getByPath(newOrder, "encounter/uuid"));
		assertEquals(order.get("orderer"), Util.getByPath(newOrder, "orderer/uuid"));
		assertEquals(order.get("specimenSource"), Util.getByPath(newOrder, "specimenSource/uuid"));
		assertNull(Util.getByPath(newOrder, "laterality"));
		assertEquals(order.get("clinicalHistory"), Util.getByPath(newOrder, "clinicalHistory"));
		assertEquals(order.get("frequency"), Util.getByPath(newOrder, "frequency/uuid"));
		assertEquals(order.get("numberOfRepeats"), Util.getByPath(newOrder, "numberOfRepeats").toString());
	}
	
	@Test
	public void shouldDiscontinueAnActiveOrder() throws Exception {
		Order orderToDiscontinue = orderService.getOrder(111);
		Patient patient = orderToDiscontinue.getPatient();
		List<Order> originalActiveOrders = orderService.getActiveOrders(patient, null, null, null);
		assertTrue(originalActiveOrders.contains(orderToDiscontinue));
		
		SimpleObject dcOrder = new SimpleObject();
		dcOrder.add("type", "order");
		dcOrder.add("action", "DISCONTINUE");
		dcOrder.add("patient", patient.getUuid());
		dcOrder.add("concept", orderToDiscontinue.getConcept().getUuid());
		dcOrder.add("careSetting", orderToDiscontinue.getCareSetting().getUuid());
		dcOrder.add("previousOrder", orderToDiscontinue.getUuid());
		dcOrder.add("encounter", "e403fafb-e5e4-42d0-9d11-4f52e89d148c");
		dcOrder.add("startDate", "2009-08-19");
		dcOrder.add("orderer", "c2299800-cca9-11e0-9572-0800200c9a66");
		dcOrder.add("orderReasonNonCoded", "Patient is allergic");
		
		SimpleObject saveDCOrder = deserialize(handle(newPostRequest(getURI(), dcOrder)));
		
		List<Order> newActiveOrders = orderService.getActiveOrders(patient, null, null, null);
		assertEquals(originalActiveOrders.size() - 1, newActiveOrders.size());
		assertFalse(newActiveOrders.contains(orderToDiscontinue));
		assertNotNull(PropertyUtils.getProperty(saveDCOrder, "orderNumber"));
		assertEquals(dcOrder.get("action"), Util.getByPath(saveDCOrder, "action"));
		assertEquals(orderToDiscontinue.getPatient().getUuid(), Util.getByPath(saveDCOrder, "patient/uuid"));
		assertEquals(orderToDiscontinue.getCareSetting().getUuid(), Util.getByPath(saveDCOrder, "careSetting/uuid"));
		assertEquals(dcOrder.get("previousOrder"), Util.getByPath(saveDCOrder, "previousOrder/uuid"));
		assertThat(Util.getByPath(saveDCOrder, "startDate").toString(), sameDatetime(dcOrder.get("startDate").toString()));
		assertEquals(orderToDiscontinue.getConcept().getUuid(), Util.getByPath(saveDCOrder, "concept/uuid"));
		assertEquals(dcOrder.get("encounter"), Util.getByPath(saveDCOrder, "encounter/uuid"));
		assertEquals(dcOrder.get("orderer"), Util.getByPath(saveDCOrder, "orderer/uuid"));
		assertEquals(dcOrder.get("orderReasonNonCoded"), Util.getByPath(saveDCOrder, "orderReasonNonCoded"));
	}
	
	@Test
	public void shouldReviseAnActiveOrder() throws Exception {
		Order orderToRevise = orderService.getOrder(7);
		Patient patient = orderToRevise.getPatient();
		List<Order> originalActiveOrders = orderService.getActiveOrders(patient, null, null, null);
		assertTrue(originalActiveOrders.contains(orderToRevise));
		
		EncounterService es = Context.getEncounterService();
		Date date = new Date();
		Encounter encounter = new Encounter();
		encounter.setEncounterType(es.getEncounterType(1));
		encounter.setPatient(patient);
		encounter.setEncounterDatetime(date);
		es.saveEncounter(encounter);
		
		SimpleObject revisedOrder = new SimpleObject();
		revisedOrder.add("type", "order");
		revisedOrder.add("action", "REVISE");
		revisedOrder.add("previousOrder", orderToRevise.getUuid());
		revisedOrder.add("patient", patient.getUuid());
		revisedOrder.add("careSetting", orderToRevise.getCareSetting().getUuid());
		revisedOrder.add("concept", orderToRevise.getConcept().getUuid());
		revisedOrder.add("startDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date));
		revisedOrder.add("encounter", encounter.getUuid());
		revisedOrder.add("orderer", "c2299800-cca9-11e0-9572-0800200c9a66");
		revisedOrder.add("instructions", "To be taken after a meal");
		revisedOrder.add("orderReasonNonCoded", "Changed instructions");
		
		SimpleObject savedOrder = deserialize(handle(newPostRequest(getURI(), revisedOrder)));
		
		List<Order> newActiveOrders = orderService.getActiveOrders(patient, null, null, null);
		assertEquals(originalActiveOrders.size(), newActiveOrders.size());
		assertFalse(newActiveOrders.contains(orderToRevise));
		assertNotNull(PropertyUtils.getProperty(savedOrder, "orderNumber"));
		assertEquals(revisedOrder.get("action"), Util.getByPath(savedOrder, "action"));
		assertEquals(patient.getUuid(), Util.getByPath(savedOrder, "patient/uuid"));
		assertEquals(orderToRevise.getCareSetting().getUuid(), Util.getByPath(savedOrder, "careSetting/uuid"));
		assertEquals(revisedOrder.get("previousOrder"), Util.getByPath(savedOrder, "previousOrder/uuid"));
		assertThat(Util.getByPath(savedOrder, "startDate").toString(),
		    sameDatetime(revisedOrder.get("startDate").toString()));
		assertEquals(revisedOrder.get("concept"), Util.getByPath(savedOrder, "concept/uuid"));
		assertEquals(revisedOrder.get("encounter"), Util.getByPath(savedOrder, "encounter/uuid"));
		assertEquals(revisedOrder.get("orderer"), Util.getByPath(savedOrder, "orderer/uuid"));
		assertEquals(revisedOrder.get("instructions"), Util.getByPath(savedOrder, "instructions"));
		assertEquals(revisedOrder.get("orderReasonNonCoded"), Util.getByPath(savedOrder, "orderReasonNonCoded"));
	}
	
	@Test
	public void shouldGetTheActiveOrdersForAPatient() throws Exception {
		String[] expectedOrderUuids = { orderService.getOrder(3).getUuid(), orderService.getOrder(5).getUuid(),
		        orderService.getOrder(7).getUuid(), orderService.getOrder(222).getUuid(),
		        orderService.getOrder(444).getUuid() };
		SimpleObject results = deserialize(handle(newGetRequest(getURI(), new Parameter("patient",
		        "da7f524f-27ce-4bb2-86d6-6d1d05312bd5"))));
		assertEquals(expectedOrderUuids.length, Util.getResultsSize(results));
		List<Object> resultList = Util.getResultsList(results);
		List<String> uuids = Arrays.asList(new String[] { PropertyUtils.getProperty(resultList.get(0), "uuid").toString(),
		        PropertyUtils.getProperty(resultList.get(1), "uuid").toString(),
		        PropertyUtils.getProperty(resultList.get(2), "uuid").toString(),
		        PropertyUtils.getProperty(resultList.get(3), "uuid").toString(),
		        PropertyUtils.getProperty(resultList.get(4), "uuid").toString() });
		assertThat(uuids, hasItems(expectedOrderUuids));
	}
	
	@Test
	public void shouldGetTheActiveOrdersForAPatientInTheSpecifiedCareSetting() throws Exception {
		String expectedOrderUuid = orderService.getOrder(222).getUuid();
		SimpleObject results = deserialize(handle(newGetRequest(getURI(), new Parameter("patient",
		        "da7f524f-27ce-4bb2-86d6-6d1d05312bd5"),
		    new Parameter("careSetting", "2ed1e57d-9f18-41d3-b067-2eeaf4b30fb2"))));
		assertEquals(1, Util.getResultsSize(results));
		assertEquals(expectedOrderUuid, PropertyUtils.getProperty(Util.getResultsList(results).get(0), "uuid"));
	}
	
	@Test
	public void shouldGetTheActiveOrdersForAPatientAsOfTheSpecifiedDate() throws Exception {
		String expectedOrderUuid = orderService.getOrder(2).getUuid();
		SimpleObject results = deserialize(handle(newGetRequest(getURI(), new Parameter("patient",
		        "da7f524f-27ce-4bb2-86d6-6d1d05312bd5"), new Parameter("asOfDate", "2007-12-10"))));
		assertEquals(1, Util.getResultsSize(results));
		assertEquals(expectedOrderUuid, PropertyUtils.getProperty(Util.getResultsList(results).get(0), "uuid"));
	}
	
	@Test
	public void shouldGetTheActiveDrugOrdersForAPatient() throws Exception {
		Method m = DrugOrderSubclassHandler1_10.class.getMethod("getActiveOrders", new Class[] { Patient.class,
		        RequestContext.class });
		assertNotNull(m);
		String[] expectedOrderUuids = { orderService.getOrder(3).getUuid(), orderService.getOrder(5).getUuid() };
		SimpleObject results = deserialize(handle(newGetRequest(getURI(), new Parameter(
		        RestConstants.REQUEST_PROPERTY_FOR_TYPE, "drugorder"), new Parameter("patient",
		        "da7f524f-27ce-4bb2-86d6-6d1d05312bd5"))));
		assertEquals(expectedOrderUuids.length, Util.getResultsSize(results));
		List<Object> resultList = Util.getResultsList(results);
		List<String> uuids = Arrays.asList(new String[] { PropertyUtils.getProperty(resultList.get(0), "uuid").toString(),
		        PropertyUtils.getProperty(resultList.get(1), "uuid").toString() });
		assertThat(uuids, hasItems(expectedOrderUuids));
	}
	
	@Test
	public void shouldGetTheActiveTestOrdersForAPatient() throws Exception {
		String expectedOrderUuid = orderService.getOrder(7).getUuid();
		SimpleObject results = deserialize(handle(newGetRequest(getURI(), new Parameter(
		        RestConstants.REQUEST_PROPERTY_FOR_TYPE, "testorder"), new Parameter("patient",
		        "da7f524f-27ce-4bb2-86d6-6d1d05312bd5"))));
		assertEquals(1, Util.getResultsSize(results));
		assertEquals(expectedOrderUuid, PropertyUtils.getProperty(Util.getResultsList(results).get(0), "uuid"));
	}
	
}
