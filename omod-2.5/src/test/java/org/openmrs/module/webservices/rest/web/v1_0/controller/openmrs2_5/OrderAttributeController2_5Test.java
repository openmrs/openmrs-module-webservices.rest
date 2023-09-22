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
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderAttribute;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.v1_0.resource.RestTestConstants2_5;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;


/**
 * Tests functionality of {@link OrderAttributeController}.
 */
public class OrderAttributeController2_5Test extends MainResourceControllerTest {
	
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
		return RestTestConstants2_5.ORDER_ATTRIBUTE_UUID;
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
	public void shouldAddAttributeToOrder() throws Exception {
		int before = service.getOrderByUuid(RestTestConstants2_5.ORDER_UUID).getAttributes().size();

		String json = "{\"type\":\"order\",\"concept\":\"d144d24f-6913-4b63-9660-a9108c2bebef\",\"orderer\":\"c2299800-cca9-11e0-9572-0800200c9a66\",\"patient\":\"5946f880-b197-400b-9caa-a3c661d23041\",\"careSetting\":\"6f0c9a92-6f24-11e3-af88-005056821db0\",\"urgency\":\"STAT\",\"dose\":30,\"doseUnits\":\"5a2aa3db-68a3-11e3-bd76-0800271c1b75\",\"frequency\":\"28090760-7c38-11e3-baa7-0800200c9a66\",\"asNeeded\":true,\"quantityUnits\":\"5a2aa3db-68a3-11e3-bd76-0800271c1b75\",\"numRefills\":1,\"duration\":30,\"durationUnits\":\"7bfdcbf0-d9e7-11e3-9c1a-0800200c9a66\",\"route\":\"e10ffe54-5184-4efe-8960-cd565ec1cdf8\",\"dispenseAsWritten\":true,\"attributes\":[{\"attributeType\":\"6f09f118-bb2e-4459-8815-bd786d5e0e59\",\"value\":\"167ce20c-4785-4285-9119-d197268f7f4a\"}]}";
		handle(newPostRequest(getURI(), json));
		int after = service.getOrderByUuid(RestTestConstants2_5.ORDER_UUID).getAttributes().size();
		Assert.assertEquals(before + 1, after);
	}

}
