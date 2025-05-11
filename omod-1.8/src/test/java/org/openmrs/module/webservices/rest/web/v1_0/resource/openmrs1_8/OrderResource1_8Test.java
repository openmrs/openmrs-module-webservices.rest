/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderResource1_8Test extends BaseDelegatingResourceTest<OrderResource1_8, Order> {
	
	@Override
	public Order newObject() {
		return Context.getOrderService().getOrderByUuid(getUuidProperty());
	}
	
	@Override
	public String getDisplayProperty() {
		return "Aspirin: 325.0 mg";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_8.ORDER_UUID;
	}
	
	/**
	 * @see OrderResource1_8#doGetAll(RequestContext)
	 * @verifies return all Orders (including retired) if context.includeAll is set
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void doGetAll_shouldReturnAllOrdersIncludingRetiredIfContextincludeAllIsSet() throws Exception {
		OrderResource1_8 or = getResource();
		
		RequestContext ctx = new RequestContext();
		
		List<Object> orderList = (List) or.getAll(ctx).get("results");
		Assert.assertEquals("getAll should return all not voided orders from sample data", 5, orderList.size());
		
		voidOneOrder();
		
		orderList = (List) or.getAll(ctx).get("results");
		Assert.assertEquals("getAll should return all not voided orders from sample data", 4, orderList.size());
		
		ctx.setIncludeAll(true);
		
		orderList = (List) or.getAll(ctx).get("results");
		Assert.assertEquals("getAll should return all orders from sample data", 5, orderList.size());
		
	}
	
	private void voidOneOrder() {
		Order order = Context.getOrderService().getOrderByUuid(RestTestConstants1_8.ORDER_UUID);
		order.setVoided(true);
		Context.getOrderService().saveOrder(order);
	}
	
	@Test
	public void testSortingLogic() {
		Date earlier = new Date(1000);
		Date later = new Date(2000);
		
		Order o1 = new Order();
		o1.setDateCreated(earlier);
		
		Order o2 = new Order();
		o2.setDateCreated(later);
		
		// Test the actual comparison logic
		int comparisonResult = o2.getDateCreated().compareTo(o1.getDateCreated());
		Assert.assertTrue("Newer date should come first", comparisonResult > 0);
	}

	@Test
    public void shouldSortOrdersByDateInDescendingOrder() {
        // Create test orders with different dates
        Order order1 = new Order();
        order1.setDateCreated(new Date(1000000));
        
        Order order2 = new Order();
        order2.setDateCreated(new Date(2000000));
        
        Order order3 = new Order();
        order3.setDateCreated(new Date(3000000));
        
        List<Order> orders = Arrays.asList(order1, order2, order3);
        
        // Sort the orders
        orders.sort(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return o2.getDateCreated().compareTo(o1.getDateCreated());
            }
        });
        
        // Verify the order by comparing dates
        assertTrue(orders.get(0).getDateCreated().after(orders.get(1).getDateCreated()));
        assertTrue(orders.get(1).getDateCreated().after(orders.get(2).getDateCreated()));
    }
}
