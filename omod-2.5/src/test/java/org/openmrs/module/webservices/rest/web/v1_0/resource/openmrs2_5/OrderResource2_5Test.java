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
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.RestTestConstants2_5;

public class OrderResource2_5Test extends BaseDelegatingResourceTest<OrderResource2_5, Order> {

    @Before
    public void before() throws Exception {
        executeDataSet(RestTestConstants2_5.ORDER_DATA_SET);
    }

    @Override
    public Order newObject() {
        return Context.getOrderService().getOrderByUuid(getUuidProperty());
    }

    @Override
    public String getDisplayProperty() {
        return "(NEW) ASPIRIN: 325.0 mg UNKNOWN 1/day x 7 days/week";
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants2_5.ORDER_UUID;
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropPresent("attributes");
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropPresent("attributes");
    }
}
