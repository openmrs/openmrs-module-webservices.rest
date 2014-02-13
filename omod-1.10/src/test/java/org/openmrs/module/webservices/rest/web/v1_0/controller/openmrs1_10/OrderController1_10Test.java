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

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Tests functionality of {@link org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10.OrderResource1_10}. This does not use @should annotations because
 * the controller inherits those methods from a subclass
 */
public class OrderController1_10Test extends MainResourceControllerTest {

    private OrderService service;

    @Before
    public void before() {
        this.service = Context.getOrderService();
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
        return RestTestConstants1_8.ORDER_UUID;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
     */
    @Override
    public long getAllCount() {
        return service.getOrders(Order.class, null, null, null, null).size();
    }

    @Test
    public void shouldCreateDiscontinueOrder() throws Exception {
        SimpleObject order = new SimpleObject();
        order.add("action", "DISCONTINUE");
        order.add("previousOrder", RestTestConstants1_8.ORDER_UUID);
        order.add("patient", "5946f880-b197-400b-9caa-a3c661d23041");
        order.add("startDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
        
        String conceptUuidForPreviousOrder = "15f83cd6-64e9-4e06-a5f9-364d3b14a43d";
        order.add("concept", conceptUuidForPreviousOrder);

        SimpleObject response = deserialize(handle(newPostRequest(getURI(), order)));

        assertEquals(order.get("action"), PropertyUtils.getProperty(response, "action"));
        assertEquals(order.get("concept"), Util.getByPath(response, "concept/uuid"));
        assertEquals(order.get("patient"), Util.getByPath(response, "patient/uuid"));
        assertEquals(order.get("previousOrder"), Util.getByPath(response, "previousOrder/uuid"));
    }

    @Override
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void shouldGetAll() throws Exception {
        super.shouldGetAll();
    }
    
}
