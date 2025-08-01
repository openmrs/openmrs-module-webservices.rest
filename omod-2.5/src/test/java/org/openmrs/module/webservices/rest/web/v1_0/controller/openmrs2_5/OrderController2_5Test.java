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

import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.OrderAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;

public class OrderController2_5Test extends RestControllerTestUtils {

    private static final String CONCEPT_UUID = "conc-uuid";
    private static final String PATIENT_UUID = "pati-uuid";
    private static final String PROVIDER_UUID = "prov-uuid";
    private static final String ENCOUNTER_UUID = "enco-uuid";
    private static final String ATTRIBUTE_TYPE_UUID = "attr-type-uuid-";

    @Test
    public void shouldCreateOrderWithAttributes() throws Exception {
        String json = "{"
                + "\"type\":\"testorder\","
                + "\"action\":\"NEW\","
                + "\"urgency\":\"ROUTINE\","
                + "\"dateActivated\":\"2018-10-16T12:08:43.000+0000\","
                + "\"careSetting\":\"" + "INPATIENT" + "\","
                + "\"encounter\":\"" + ENCOUNTER_UUID + "\","
                + "\"patient\":\"" + PATIENT_UUID + "\","
                + "\"concept\":\"" + CONCEPT_UUID + "\","
                + "\"orderer\":\"" + PROVIDER_UUID + "\","
                + "\"attributes\":["
                + "   {\"attributeType\":\"" + ATTRIBUTE_TYPE_UUID + "\",\"value\":\"Test Value\"}"
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
        assertEquals(ATTRIBUTE_TYPE_UUID, attr.getAttributeType().getUuid());
    }
}
