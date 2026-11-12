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
        return service.getAllOrderAttributeTypes().stream().filter(type -> !type.getRetired()).count();
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
    @Test
    public void shouldIncludeRetiredTypesOnlyWhenRequested() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("includeAll", "true");
        SimpleObject result = deserialize(handle(req));
        Assertions.assertEquals(service.getAllOrderAttributeTypes().size(), Util.getResultsSize(result));
    }

    @Test
    public void shouldSearchCaseInsensitivelyAndExcludeRetiredTypes() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("q", "SAMPLE");
        SimpleObject result = deserialize(handle(req));
        Assertions.assertEquals(1, Util.getResultsSize(result));
    }

    @Test
    public void shouldIncludeRetiredTypesInSearchWhenRequested() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("q", "");
        req.addParameter("includeAll", "true");
        SimpleObject result = deserialize(handle(req));
        Assertions.assertEquals(service.getAllOrderAttributeTypes().size(), Util.getResultsSize(result));
    }
    private OrderAttributeType createType(String name) {
        OrderAttributeType type = new OrderAttributeType();
        type.setName(name);
        type.setDatatypeClassname("org.openmrs.customdatatype.datatype.FreeTextDatatype");
        type.setMinOccurs(0);
        type.setMaxOccurs(1);
        return service.saveOrderAttributeType(type);
    }

    @Test
    public void shouldPagePrefixMatchesBeforeContainingMatchesAndReturnTotalCount() throws Exception {
        createType("A sample containing match");
        OrderAttributeType prefix = createType("Sample prefix match");
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("q", "SAMPLE");
        req.addParameter("limit", "1");
        req.addParameter("startIndex", "1");
        req.addParameter("totalCount", "true");
        SimpleObject result = deserialize(handle(req));
        Assertions.assertEquals(1, Util.getResultsSize(result));
        Assertions.assertEquals(prefix.getUuid(), PropertyUtils.getProperty(Util.getResultsList(result).get(0), "uuid"));
        Assertions.assertEquals(3, ((Number) result.get("totalCount")).intValue());
        Assertions.assertFalse(((java.util.List<?>) result.get("links")).isEmpty());
    }

    @Test
    public void shouldTreatSqlWildcardsAsLiteralCharacters() throws Exception {
        OrderAttributeType literal = createType("Literal %_! attribute");
        createType("Literal unrelated attribute");
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("q", "%_!");
        req.addParameter("totalCount", "true");
        SimpleObject result = deserialize(handle(req));
        Assertions.assertEquals(1, Util.getResultsSize(result));
        Assertions.assertEquals(literal.getUuid(), PropertyUtils.getProperty(Util.getResultsList(result).get(0), "uuid"));
        Assertions.assertEquals(1, ((Number) result.get("totalCount")).intValue());
    }

    @Test
    public void shouldFilterRetiredSearchMatchesAndCountConsistently() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("q", "retired");
        req.addParameter("totalCount", "true");
        SimpleObject result = deserialize(handle(req));
        Assertions.assertEquals(0, Util.getResultsSize(result));
        Assertions.assertEquals(0, ((Number) result.get("totalCount")).intValue());
        req.addParameter("includeAll", "true");
        result = deserialize(handle(req));
        Assertions.assertEquals(1, Util.getResultsSize(result));
        Assertions.assertEquals(1, ((Number) result.get("totalCount")).intValue());
    }
}
