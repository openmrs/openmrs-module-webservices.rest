/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_5;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.OrderAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.v1_0.resource.RestTestConstants2_5;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class OrderAttributeTypeController2_5Test extends MainResourceControllerTest {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "orderattributetype";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants2_5.ORDER_ATTRIBUTE_TYPE_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return Context.getOrderService().getAllOrderAttributeTypes().size();
	}
	
	@Before
	public void before() throws Exception {
		executeDataSet(RestTestConstants2_5.TEST_DATASET);
	}
	
	/**
	 * @see OrderAttributeTypeController#createOrderAttributeType(SimpleObject,WebRequest)
	 * @verifies create a new OrderAttributeType
	 */
	@Test
	public void createOrderAttributeType_shouldCreateANewOrderAttributeType() throws Exception {
		int before = Context.getOrderService().getAllOrderAttributeTypes().size();
		String json = "{ \"name\":\"Some attributeType\",\"description\":\"Attribute Type for order\",\"datatypeClassname\":\"org.openmrs.customdatatype.datatype.FreeTextDatatype\"}";
		
		handle(newPostRequest(getURI(), json));
		
		Assert.assertEquals(before + 1, Context.getOrderService().getAllOrderAttributeTypes().size());
	}
	
	/**
	 * @see OrderAttributeTypeController#updateOrderAttributeType(OrderAttributeType,SimpleObject,WebRequest)
	 * @verifies change a property on a order
	 */
	@Test
	public void updateOrderAttributeType_shouldChangeAPropertyOnAOrderAttributeType() throws Exception {
		String json = "{\"description\":\"Updated description\"}";
		
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		
		Assert.assertEquals("Updated description", Context.getOrderService().getOrderAttributeTypeById(1).getDescription());
	}
	
	/**
	 * @see OrderAttributeTypeController#retireOrderAttributeType(OrderAttributeType,String,WebRequest)
	 * @verifies void a order attribute type
	 */
	@Test
	public void retireOrderAttributeType_shouldRetireAOrderAttributeType() throws Exception {
		OrderAttributeType orderAttributeType = Context.getOrderService().getOrderAttributeTypeById(1);
		Assert.assertFalse(orderAttributeType.isRetired());
		
		MockHttpServletRequest request = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
		request.addParameter("reason", "test");
		handle(request);
		
		orderAttributeType = Context.getOrderService().getOrderAttributeTypeById(1);
		Assert.assertTrue(orderAttributeType.isRetired());
		Assert.assertEquals("test", orderAttributeType.getRetireReason());
	}
	
	/**
	 * @see OrderAttributeTypeController#findOrderAttributeTypes(String,WebRequest,HttpServletResponse)
	 * @verifies return no results if there are no matching order(s)
	 */
	@Test
	public void findOrderAttributeTypes_shouldReturnNoResultsIfThereAreNoMatchingOrders() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("q", "zzzznotype"))));
		
		Assert.assertEquals(0, Util.getResultsSize(result));
	}
	
	/**
	 * @see OrderAttributeTypeController#findOrderAttributeTypes(String,WebRequest,HttpServletResponse)
	 * @verifies find matching order attribute types
	 */
	@Test
	public void findOrderAttributeTypes_shouldFindMatchingOrderAttributeTypes() throws Exception {
		SimpleObject response = deserialize(handle(newGetRequest(getURI(), new Parameter("q", "Dispensing Location"))));
		
		List<Object> results = Util.getResultsList(response);
		
		Assert.assertEquals(1, results.size());
		Util.log("Found " + results.size() + " OrderAttributeType(s)", results);
		Object result = results.get(0);
		Assert.assertEquals(RestTestConstants2_5.ORDER_ATTRIBUTE_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
	}
	
}
