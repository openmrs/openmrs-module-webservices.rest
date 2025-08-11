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

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.OrderAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.RestTestConstants2_5;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;

public class OrderController2_5Test extends MainResourceControllerTest {

    private static final String CONCEPT_UUID = RestTestConstants2_5.CONCEPT_UUID;
    private static final String PATIENT_UUID = RestTestConstants2_5.PATIENT_UUID;
    private static final String PROVIDER_UUID = RestTestConstants2_5.PROVIDER_UUID;
    private static final String ENCOUNTER_UUID = RestTestConstants2_5.ENCOUNTER_UUID;
    private static final String ORDER_UUID = RestTestConstants2_5.ORDER_UUID;
    private static final String ORDER_ATTRIBUTE_TYPE_UUID = RestTestConstants2_5.ORDER_ATTRIBUTE_TYPE_UUID;

    @Before
    public void before() throws Exception {
        executeDataSet(RestTestConstants2_5.ORDER_DATA_SET);
    }

    /**
     * @see MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        return "order";
    }

    /**
     * @see MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        return ORDER_UUID;
    }

    /**
     * @see MainResourceControllerTest#getAllCount()
     */
    @Override
    public long getAllCount() {
        return 1;
    }

    @Test
    public void shouldCreateOrderWithAttributes() throws Exception {
        String json = "{"
                + "\"type\":\"testorder\","
                + "\"action\":\"NEW\","
                + "\"urgency\":\"ROUTINE\","
                + "\"dateActivated\":\"2025-08-10 12:08:43\","
                + "\"careSetting\":\"" + "INPATIENT" + "\","
                + "\"encounter\":\"" + ENCOUNTER_UUID + "\","
                + "\"patient\":\"" + PATIENT_UUID + "\","
                + "\"concept\":\"" + CONCEPT_UUID + "\","
                + "\"orderer\":\"" + PROVIDER_UUID + "\","
                + "\"attributes\":["
                + "   {\"attributeType\":\"" + ORDER_ATTRIBUTE_TYPE_UUID + "\",\"value\":\"Test Value\"}"
                + "]"
                + "}";

        MockHttpServletResponse response = handle(newPostRequest("order", json));
        assertEquals(201, response.getStatus());

        SimpleObject created = deserialize(response);
        String orderUuid = (String) created.get("uuid");
        assertNotNull(orderUuid);

        Order order = Context.getOrderService().getOrderByUuid(orderUuid);
        assertNotNull(order);
        assertEquals(1, order.getActiveAttributes().size());

        OrderAttribute attr = order.getActiveAttributes().iterator().next();
        assertEquals("Test Value", attr.getValue());
        assertEquals(ORDER_ATTRIBUTE_TYPE_UUID, attr.getAttributeType().getUuid());
    }

    /**
     * @see MainResourceControllerTest#shouldGetAll()
     */
    @Override
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void shouldGetAll() throws Exception {
        super.shouldGetAll();
    }
}
