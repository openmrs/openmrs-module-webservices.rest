/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_12;

import org.junit.Before;
import org.openmrs.OrderSet;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_12;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

/**
 * Contains tests for the {@link OrderSetResource1_12}
 */
public class OrderSetResource1_12Test extends BaseDelegatingResourceTest<OrderSetResource1_12, OrderSet> {

    @Before
    public void before() throws Exception {
        executeDataSet(RestTestConstants1_12.TEST_DATA_SET);
    }

    @Override
    public OrderSet newObject() {
        return Context.getOrderSetService().getOrderSetByUuid(getUuidProperty());
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropEquals("name", getObject().getName());
        assertPropEquals("uuid", getObject().getUuid());
        assertPropEquals("description", getObject().getDescription());
        assertPropEquals("operator", getObject().getOperator());
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropEquals("name", getObject().getName());
        assertPropEquals("description", getObject().getDescription());
        assertPropEquals("retired", getObject().getRetired());
        assertPropEquals("retireBy", getObject().getRetiredBy());
        assertPropPresent("auditInfo");
    }

    @Override
    public String getDisplayProperty() {
        return "orderSet1";
    }

    @Override
    public String getUuidProperty() {
        return RestTestConstants1_12.ORDER_SET_UUID;
    }
}