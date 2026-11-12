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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.OrderAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.RestTestConstants2_5;
import org.springframework.web.bind.annotation.RequestMethod;

public class OrderAttributeController2_5Test extends MainResourceControllerTest {

    @BeforeEach
    public void before() throws Exception {
        executeDataSet(RestTestConstants2_5.TEST_DATASET);
    }

    @Override
    public String getURI() {
        return "order/" + RestTestConstants2_5.ORDER_UUID + "/attribute";
    }

    @Override
    public String getUuid() {
        return RestTestConstants2_5.ORDER_ATTRIBUTE_UUID;
    }

    @Override
    public long getAllCount() {
        return 3;
    }

    @Test
    public void shouldExcludeVoidedAttributesUnlessRequested() throws Exception {
        Context.getOrderService().getOrderAttributeByUuid(getUuid()).setVoided(true);
        SimpleObject active = deserialize(handle(newGetRequest(getURI())));
        SimpleObject all = deserialize(handle(newGetRequest(getURI(), new Parameter("includeAll", "true"))));
        Assertions.assertEquals(2, Util.getResultsSize(active));
        Assertions.assertEquals(3, Util.getResultsSize(all));
    }

    @Test
    public void shouldRejectCreateOnSavedOrderWithoutChangingOrder() {
        int count = Context.getOrderService().getOrderByUuid(RestTestConstants2_5.ORDER_UUID).getAttributes().size();
        Assertions.assertThrows(ResponseException.class,
                () -> handle(newPostRequest(getURI(), "{\"attributeType\":\"" + RestTestConstants2_5.ORDER_ATTRIBUTE_TYPE_UUID
                        + "\",\"value\":\"2025-01-01\"}")));
        Assertions.assertEquals(count, Context.getOrderService().getOrderByUuid(RestTestConstants2_5.ORDER_UUID).getAttributes().size());
    }

    @Test
    public void shouldRejectUpdateBeforeConvertingProperties() {
        OrderAttribute attribute = Context.getOrderService().getOrderAttributeByUuid(getUuid());
        Object original = attribute.getValue();
        Assertions.assertThrows(ResponseException.class,
                () -> handle(newPostRequest(getURI() + "/" + getUuid(), "{\"value\":\"2025-01-01\"}")));
        Assertions.assertEquals(original, attribute.getValue());
    }

    @Test
    public void shouldRejectDeleteAndPurgeWithoutChangingOrder() {
        OrderAttribute attribute = Context.getOrderService().getOrderAttributeByUuid(getUuid());
        Assertions.assertThrows(ResponseException.class,
                () -> handle(request(RequestMethod.DELETE, getURI() + "/" + getUuid())));
        Assertions.assertThrows(ResponseException.class,
                () -> handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("purge", "true"))));
        Assertions.assertFalse(attribute.getVoided());
        Assertions.assertEquals(org.openmrs.Order.Action.NEW, attribute.getOrder().getAction());
    }

    @Test
    public void shouldRejectAttributeUnderAnotherParent() {
        Assertions.assertThrows(ResponseException.class,
                () -> handle(newGetRequest("order/another-order/attribute/" + getUuid())));
    }

    @Test
    public void shouldReturnNotFoundForMissingParent() {
        Assertions.assertThrows(org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException.class,
                () -> handle(newGetRequest("order/missing-order/attribute")));
    }
}
