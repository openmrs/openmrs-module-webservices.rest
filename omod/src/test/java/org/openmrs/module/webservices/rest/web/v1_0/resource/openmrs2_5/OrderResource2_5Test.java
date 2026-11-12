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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.openmrs.OrderAttribute;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.openmrs.Order;
import org.openmrs.api.context.Context;

import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.RestTestConstants2_5;

public class OrderResource2_5Test extends BaseDelegatingResourceTest<OrderResource2_5, Order> {

	@BeforeEach
	public void before() throws Exception {
		executeDataSet(RestTestConstants2_5.TEST_DATASET);
	}

	@Override
	public Order newObject() {
		return Context.getOrderService().getOrderByUuid(getUuidProperty());
	}

	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropPresent("attributes");
	}

	@Override
	public String getDisplayProperty() {
		return "(NEW) ASPIRIN: 30.0 mg UNKNOWN 1/day x 7 days/week 30 day";
	}

	@Override
	public String getUuidProperty() {
		return RestTestConstants2_5.ORDER_UUID;
	}
    @Test
    public void shouldUpdateExistingAttributeByUuidWithoutRemovingOmittedAttributes() {
        Order order = newObject();
        OrderAttribute existing = Context.getOrderService().getOrderAttributeByUuid("3a2bdb18-nadm-11e0-8414-001e378eb67e");
        OrderAttribute update = new OrderAttribute();
        update.setUuid(existing.getUuid());
        update.setAttributeType(existing.getAttributeType());
        update.setValue("Updated organization");
        int originalCount = order.getAttributes().size();
        OrderResource2_5.setAttributes(order, Collections.singletonList(update));
        Assertions.assertEquals(originalCount, order.getAttributes().size());
        Assertions.assertTrue(order.getAttributes().contains(existing));
        Assertions.assertEquals("Updated organization", existing.getValue());
        Assertions.assertEquals("Updated organization", existing.getValueReference());
        Assertions.assertTrue(order.getAttributes().stream().noneMatch(OrderAttribute::getVoided));
    }

    @Test
    public void shouldMergeConvertedAttributeUpdateWithoutDuplicatingIt() {
        Order order = newObject();
        OrderAttribute existing = Context.getOrderService().getOrderAttributeByUuid("3a2bdb18-nadm-11e0-8414-001e378eb67e");
        Map<String, Object> values = new HashMap<>();
        values.put("uuid", existing.getUuid());
        values.put("value", "Updated organization");
        OrderAttribute update = (OrderAttribute) ConversionUtil.convertMap(values, OrderAttribute.class);
        int originalCount = order.getAttributes().size();
        OrderResource2_5.setAttributes(order, Collections.singletonList(update));
        Assertions.assertEquals(originalCount, order.getAttributes().size());
        Assertions.assertEquals("Updated organization", existing.getValue());
        Assertions.assertSame(order, existing.getOrder());
    }

    @Test
    public void shouldPreserveAllAttributesWhenSubmittedListIsEmpty() {
        Order order = newObject();
        int originalCount = order.getAttributes().size();
        OrderResource2_5.setAttributes(order, Collections.emptyList());
        Assertions.assertEquals(originalCount, order.getAttributes().size());
    }

    @Test
    public void shouldAddNewAttributeWithoutReplacingOtherAttributes() {
        Order order = newObject();
        OrderAttribute attribute = new OrderAttribute();
        attribute.setAttributeType(Context.getOrderService().getOrderAttributeTypeByUuid("9516cc50-6f9f-132r-6556-001e378eb67f"));
        attribute.setValue("Another organization");
        int originalCount = order.getAttributes().size();
        OrderResource2_5.setAttributes(order, Collections.singletonList(attribute));
        Assertions.assertEquals(originalCount + 1, order.getAttributes().size());
        Assertions.assertSame(order, attribute.getOrder());
        Assertions.assertEquals("Another organization", attribute.getValueReference());
    }

    @Test
    public void shouldRejectAttributeFromAnotherOrder() {
        Order order = newObject();
        OrderAttribute attribute = new OrderAttribute();
        attribute.setOrder(new Order());
        int originalCount = order.getAttributes().size();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> OrderResource2_5.setAttributes(order, Collections.singletonList(attribute)));
        Assertions.assertEquals(originalCount, order.getAttributes().size());
    }
}
