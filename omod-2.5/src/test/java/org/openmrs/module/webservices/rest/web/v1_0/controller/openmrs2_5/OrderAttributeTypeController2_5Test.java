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

    @Before
    public void before() throws Exception {
        executeDataSet(RestTestConstants2_5.ORDER_ATTRIBUTE_DATA_SET);
        this.service = Context.getOrderService();
    }

    @Test
    public void shouldCreateOrderAttributeType() throws Exception {
        long originalCount = Context.getOrderService().getAllOrderAttributeTypes().size();

        String json = "{\"name\": \"Order Duration\"," + "\"description\": \"Captures how long an order should be maintained\"," +
                "\"datatypeClassname\": \"org.openmrs.customdatatype.datatype.LongFreeTextDatatype\"," + "\"minOccurs\": 0," + "\"maxOccurs\": 1," + "\"datatypeConfig\": \"default\"," +
                "\"preferredHandlerClassname\": \"org.openmrs.web.attribute.handler.LongFreeTextTextareaHandler\"," + "\"handlerConfig\": null}";

        MockHttpServletRequest req = request(RequestMethod.POST, "orderattributetype");
        req.setContent(json.getBytes());

        Object newOrderAttributeType = deserialize(handle(req));
        Assert.assertNotNull(PropertyUtils.getProperty(newOrderAttributeType, "uuid"));
        Assert.assertEquals(originalCount + 1, Context.getOrderService().getAllOrderAttributeTypes().size());
    }

    @Test
    public void shouldPurgeOrderAttributeType() throws Exception {
        final String UUID = "cfc96e8e-1234-4c44-aaaa-abcdef123456";
        Assert.assertNotNull(Context.getOrderService().getOrderAttributeTypeByUuid(UUID));
        MockHttpServletRequest req = request(RequestMethod.DELETE, "orderattributetype/" + UUID);
        req.addParameter("purge", "true");
        handle(req);
        Assert.assertNull(Context.getOrderService().getOrderAttributeTypeByUuid(UUID));
    }

    @Test
    public void shouldListAllOrderAttributeTypes() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        SimpleObject result = deserialize(handle(req));

        Assert.assertNotNull(result);
        Assert.assertEquals(getAllCount(), Util.getResultsSize(result));
    }

    @Test
    public void shouldUpdateOrderAttributeType() throws Exception {
        final String ORDER_ATTRIBUTE_TYPE_UUID = "cfc96e8e-1234-4c44-aaaa-abcdef123456";
        OrderAttributeType existingOrderAttributeType = Context.getOrderService().getOrderAttributeTypeByUuid(ORDER_ATTRIBUTE_TYPE_UUID);
        Assert.assertNotNull(existingOrderAttributeType);

        String json = "{\"name\": \"Updated Order Attribute\"," + " \"description\": \"Updated description for order attribute\"," + " \"datatypeClassname\": \"org.openmrs.customdatatype.datatype.LongFreeTextDatatype\"}";

        handle(newPostRequest("orderattributetype/" + existingOrderAttributeType.getUuid(), json));
        OrderAttributeType updatedOrderAttributeType = Context.getOrderService().getOrderAttributeTypeByUuid(ORDER_ATTRIBUTE_TYPE_UUID);

        Assert.assertNotNull(updatedOrderAttributeType);
        Assert.assertEquals("Updated Order Attribute", updatedOrderAttributeType.getName());
        Assert.assertEquals("Updated description for order attribute", updatedOrderAttributeType.getDescription());
        Assert.assertEquals("org.openmrs.customdatatype.datatype.LongFreeTextDatatype", updatedOrderAttributeType.getDatatypeClassname());
    }

    @Test
    public void shouldGetAOrderAttributeTypeByUuid() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
        SimpleObject result = deserialize(handle(req));

        OrderAttributeType orderAttributeType = service.getOrderAttributeTypeByUuid(getUuid());
        Assert.assertEquals(orderAttributeType.getUuid(), PropertyUtils.getProperty(result, "uuid"));
        Assert.assertEquals(orderAttributeType.getName(), PropertyUtils.getProperty(result, "name"));
    }

    @Test
    public void shouldRetireOrderAttributeType() throws Exception {
        final String UUID = "cfc96e8e-1234-4c44-aaaa-abcdef123456";
        OrderAttributeType orderAttributeType = Context.getOrderService().getOrderAttributeTypeByUuid(UUID);
        Assert.assertNotNull(orderAttributeType);
        Assert.assertFalse(orderAttributeType.getRetired());
        Assert.assertNull(orderAttributeType.getDateRetired());
        Assert.assertNull(orderAttributeType.getRetiredBy());

        MockHttpServletRequest req = request(RequestMethod.DELETE, "orderattributetype/" + UUID);
        req.addParameter("!purge", "");
        req.addParameter("reason", "let it retire for a while");
        handle(req);

        orderAttributeType = Context.getOrderService().getOrderAttributeTypeByUuid(UUID);
        Assert.assertTrue(orderAttributeType.getRetired());
        Assert.assertNotNull(orderAttributeType.getDateRetired());
        Assert.assertNotNull(orderAttributeType.getRetiredBy());
        Assert.assertEquals("let it retire for a while", orderAttributeType.getRetireReason());
    }
}
