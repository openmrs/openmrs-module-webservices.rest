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
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.RestTestConstants2_5;
import org.springframework.web.bind.annotation.RequestMethod;

public class OrderAttributeScopeController2_5Test extends MainResourceControllerTest {
    @BeforeEach
    public void before() throws Exception {
        executeDataSet(RestTestConstants2_5.TEST_DATASET);
    }

    @Override
    public String getURI() { return "order"; }

    @Override
    public String getUuid() { return RestTestConstants2_5.ORDER_UUID; }

    @Override
    public long getAllCount() { return 0; }

    @Override
    @Test
    public void shouldGetAll() {
        Assertions.assertThrows(ResponseException.class, () -> handle(request(RequestMethod.GET, getURI())));
    }

    @Test
    public void shouldReadOrderAttributesInDefaultAndFullRepresentations() throws Exception {
        SimpleObject defaultOrder = deserialize(handle(newGetRequest(getURI() + "/" + getUuid())));
        SimpleObject fullOrder = deserialize(handle(newGetRequest(getURI() + "/" + getUuid(), new Parameter("v", "full"))));
        Assertions.assertEquals(3, ((java.util.List<?>) defaultOrder.get("attributes")).size());
        Assertions.assertEquals(3, ((java.util.List<?>) fullOrder.get("attributes")).size());
    }

    @Test
    public void shouldRejectEmbeddedAttributeUpdateOnSavedOrder() {
        OrderAttribute attribute = Context.getOrderService().getOrderAttributeByUuid(RestTestConstants2_5.ORDER_ATTRIBUTE_UUID);
        Object original = attribute.getValue();
        Assertions.assertThrows(ResponseException.class,
                () -> handle(newPostRequest(getURI() + "/" + getUuid(), "{\"attributes\":[{\"uuid\":\""
                        + attribute.getUuid() + "\",\"value\":\"2025-01-01\"}]}")));
        Assertions.assertEquals(original, attribute.getValue());
    }
}
