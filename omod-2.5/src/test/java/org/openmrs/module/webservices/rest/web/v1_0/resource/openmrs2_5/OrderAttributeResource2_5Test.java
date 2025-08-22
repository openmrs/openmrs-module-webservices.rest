/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_5;

import org.junit.Before;
import org.openmrs.OrderAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.RestTestConstants2_5;

public class OrderAttributeResource2_5Test extends BaseDelegatingResourceTest<OrderAttributeResource2_5, OrderAttribute> {

    @Before
    public void before() throws Exception {
        executeDataSet(RestTestConstants2_5.ORDER_DATA_SET);
    }

    @Override
    public OrderAttribute newObject() {
        return Context.getOrderService().getOrderAttributeByUuid(getUuidProperty());
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropEquals("value", getObject().getValue());
        assertPropPresent("attributeType");
        assertPropEquals("voided", getObject().getVoided());
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropEquals("value", getObject().getValue());
        assertPropPresent("attributeType");
        assertPropEquals("voided", getObject().getVoided());
        assertPropPresent("auditInfo");
    }

    @Override
    public String getDisplayProperty() {
        return "Test Attribute Type: Test";
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants2_5.ORDER_ATTRIBUTE_UUID;
    }
}