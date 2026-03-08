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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.OrderAttributeType;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.v1_0.RestTestConstants2_5;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class OrderAttributeTypeController2_5Test extends MainResourceControllerTest {

    private OrderService service;

    /**
     * @see MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        return "orderattributetype";
    }

    /**
     * @see MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        return RestTestConstants2_5.ORDER_ATTRIBUTE_TYPE_UUID;
    }

    /**
     * @see MainResourceControllerTest#getAllCount()
     */
    @Override
    public long getAllCount() {
        return service.getAllOrderAttributeTypes().size();
    }

    @BeforeEach
    public void before() throws Exception {
        executeDataSet(RestTestConstants2_5.ORDER_ATTRIBUTE_DATA_SET);
        this.service = Context.getOrderService();
    }

    @Test
    public void shouldCreateOrderAttributeType() throws Exception {
        long originalCount = service.getAllOrderAttributeTypes().size();

        String json = "{\"name\": \"Order Duration\"," + "\"description\": \"Captures how long an order should be maintained\"," +
                "\"datatypeClassname\": \"org.openmrs.customdatatype.datatype.LongFreeTextDatatype\"," + "\"minOccurs\": 0," + "\"maxOccurs\": 1," + "\"datatypeConfig\": \"default\"," +
                "\"preferredHandlerClassname\": \"org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler\"," + "\"handlerConfig\": null}";

        MockHttpServletRequest req = request(RequestMethod.POST, getURI());
        req.setContent(json.getBytes());

        Object newOrderAttributeType = deserialize(handle(req));
        Assertions.assertNotNull(PropertyUtils.getProperty(newOrderAttributeType, "uuid"));
        Assertions.assertEquals(originalCount + 1, service.getAllOrderAttributeTypes().size());
    }

    @Test
    public void shouldPurgeOrderAttributeType() throws Exception {
        final String UUID = "cfc96e8e-1234-4c44-aaaa-abcdef123456";
        Assertions.assertNotNull(service.getOrderAttributeTypeByUuid(UUID));
        MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + UUID);
        req.addParameter("purge", "true");
        handle(req);
        Assertions.assertNull(service.getOrderAttributeTypeByUuid(UUID));
    }

    @Test
    public void shouldListAllOrderAttributeTypes() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        SimpleObject result = deserialize(handle(req));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(getAllCount(), Util.getResultsSize(result));
    }

    @Test
    public void shouldUpdateOrderAttributeType() throws Exception {
        final String ORDER_ATTRIBUTE_TYPE_UUID = "cfc96e8e-1234-4c44-aaaa-abcdef123456";
        OrderAttributeType existingOrderAttributeType = service.getOrderAttributeTypeByUuid(ORDER_ATTRIBUTE_TYPE_UUID);
        Assertions.assertNotNull(existingOrderAttributeType);

        String json = "{\"name\": \"Updated Order Attribute\"," + " \"description\": \"Updated description for order attribute\"," + " \"datatypeClassname\": \"org.openmrs.customdatatype.datatype.LongFreeTextDatatype\"}";

        handle(newPostRequest(getURI() + "/" + existingOrderAttributeType.getUuid(), json));
        OrderAttributeType updatedOrderAttributeType = service.getOrderAttributeTypeByUuid(ORDER_ATTRIBUTE_TYPE_UUID);

        Assertions.assertNotNull(updatedOrderAttributeType);
        Assertions.assertEquals("Updated Order Attribute", updatedOrderAttributeType.getName());
        Assertions.assertEquals("Updated description for order attribute", updatedOrderAttributeType.getDescription());
        Assertions.assertEquals("org.openmrs.customdatatype.datatype.LongFreeTextDatatype", updatedOrderAttributeType.getDatatypeClassname());
    }

    @Test
    public void shouldGetAOrderAttributeTypeByUuid() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
        SimpleObject result = deserialize(handle(req));

        OrderAttributeType orderAttributeType = service.getOrderAttributeTypeByUuid(getUuid());
        Assertions.assertEquals(orderAttributeType.getUuid(), PropertyUtils.getProperty(result, "uuid"));
        Assertions.assertEquals(orderAttributeType.getName(), PropertyUtils.getProperty(result, "name"));
    }

    @Test
    public void shouldRetireOrderAttributeType() throws Exception {
        final String UUID = "cfc96e8e-1234-4c44-aaaa-abcdef123456";
        OrderAttributeType orderAttributeType = service.getOrderAttributeTypeByUuid(UUID);
        Assertions.assertNotNull(orderAttributeType);
        Assertions.assertFalse(orderAttributeType.getRetired());
        Assertions.assertNull(orderAttributeType.getDateRetired());
        Assertions.assertNull(orderAttributeType.getRetiredBy());

        MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + UUID);
        req.addParameter("!purge", "");
        req.addParameter("reason", "let it retire for a while");
        handle(req);

        orderAttributeType = service.getOrderAttributeTypeByUuid(UUID);
        Assertions.assertTrue(orderAttributeType.getRetired());
        Assertions.assertNotNull(orderAttributeType.getDateRetired());
        Assertions.assertNotNull(orderAttributeType.getRetiredBy());
        Assertions.assertEquals("let it retire for a while", orderAttributeType.getRetireReason());
    }
}
