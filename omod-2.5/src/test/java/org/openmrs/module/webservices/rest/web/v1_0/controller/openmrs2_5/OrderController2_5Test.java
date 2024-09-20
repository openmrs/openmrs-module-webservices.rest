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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.openmrs.Order;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.RestTestConstants2_5;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


/**
 * Tests functionality of {@link OrderController}.
 */
public class OrderController2_5Test extends MainResourceControllerTest {
	
	private OrderService service;
	
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
		return RestTestConstants2_5.ORDER_UUID;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return service.getOrderByUuid(RestTestConstants2_5.ORDER_UUID).getActiveAttributes().size();
	}

	@Before
	public void before() throws Exception {
		executeDataSet(RestTestConstants2_5.TEST_DATASET);
		this.service = Context.getOrderService();
	}
	
	@Test
	public void shouldCreateOrderWithAttribute() throws Exception {
		String json = "{\"encounter\":\"d2d69553-2247-414c-b0c5-46be893605af\",\"orderType\":\"2d3fb1d0-ae06-22e3-a5e2-0140211c9a66\",\"type\":\"order\",\"action\":\"NEW\",\"accessionNumber\":\"string\",\"patient\":\"5946f880-b197-400b-9caa-a3c661d23041\",\"concept\":\"d144d24f-6913-4b63-9660-a9108c2bebef\",\"careSetting\":\"6f0c9a92-6f24-11e3-af88-005056821db0\",\"orderer\":\"c2299800-cca9-11e0-9572-0800200c9a66\",\"previousOrder\":\"\",\"urgency\":\"ROUTINE\",\"orderReason\":\"\",\"orderReasonNonCoded\":\"for Test\",\"instructions\":\"string\",\"commentToFulfiller\":\"string\",\"attributes\":[{\"attributeType\":\"c0de4f5c-6626-418e-9f4f-5396a31e68fb\",\"value\":\"2023-08-14 17:11:39\"}]}";
		handle(newPostRequest(getURI(), json));
		List<Order> orderList= Context.getOrderService().getActiveOrders(Context.getPatientService().getPatientByUuid("5946f880-b197-400b-9caa-a3c661d23041"),Context.getOrderService().getOrderTypeByUuid("2d3fb1d0-ae06-22e3-a5e2-0140211c9a66"),null,null);
		Assert.assertEquals(orderList.size(),1);
		Assert.assertEquals(orderList.get(0).getAttributes().size(),1);
		Assert.assertEquals(orderList.get(0).getAttributes().iterator().next().getAttributeType().getUuid(),"c0de4f5c-6626-418e-9f4f-5396a31e68fb");
	}

	@Test
	@Override
	public void shouldGetAll()  {
		ThrowingRunnable throwingRunnable= () -> {
			handle(request(RequestMethod.GET, getURI()));
        };
		Assert.assertThrows(ResponseException.class,throwingRunnable);
	}


}
