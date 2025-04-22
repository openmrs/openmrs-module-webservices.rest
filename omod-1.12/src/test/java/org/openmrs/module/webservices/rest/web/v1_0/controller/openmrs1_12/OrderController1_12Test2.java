/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_12;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OrderController1_12Test2 {

    private OrderService orderService;
    private static final String ORDER_URI = "/rest/v1/order";

    @Before
    public void init() throws Exception {
        orderService = Context.getOrderService();
        
        // Setup minimal test data
        OrderType drugOrderType = new OrderType();
        drugOrderType.setName("Drug order");
        orderService.saveOrderType(drugOrderType);
    }

    @Test
    public void getOrder_shouldIncludeDrugStrength() throws Exception {
        // Given
        Drug drug = new Drug();
        drug.setName("Aspirin");
        drug.setStrength("325 mg");
        DrugOrder order = createAndSaveDrugOrder(drug);
        
        // When
        MockHttpServletRequest request = newGetRequest(ORDER_URI + "/" + order.getUuid(),
            new Parameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL));
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        handle(request, response);
        
        // Then
        Map<String, Object> result = readResponse(response);
        Map<String, Object> drugRep = (Map<String, Object>) result.get("drug");
        assertNotNull("Drug representation should not be null", drugRep);
        assertEquals("325 mg", drugRep.get("strength"));
    }

    private DrugOrder createAndSaveDrugOrder(Drug drug) throws ResponseException {
        OrderType drugOrderType = orderService.getOrderTypeByName("Drug order");
        
        DrugOrder order = new DrugOrder();
        order.setDrug(drug);
        order.setOrderType(drugOrderType);
        order.setAction(Order.Action.NEW);
        
        // Cast to DrugOrder is safe here since we're creating a DrugOrder
        return (DrugOrder) orderService.saveOrder(order, null);
    }

    // Helper methods to replace BaseCrudControllerTest functionality
    private MockHttpServletRequest newGetRequest(String uri, Parameter... parameters) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        for (Parameter parameter : parameters) {
            request.addParameter(parameter.getName(), parameter.getValue());
        }
        return request;
    }

    private void handle(MockHttpServletRequest request, MockHttpServletResponse response) throws Exception {
        // Implementation would depend on your controller setup
        // This is a placeholder for actual request handling
    }

    private Map<String, Object> readResponse(MockHttpServletResponse response) throws Exception {
        // Implementation would parse the JSON response
        // This is a placeholder for actual response parsing
        return null;
    }

    public static class Parameter {
        private final String name;
        private final String value;

        public Parameter(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}