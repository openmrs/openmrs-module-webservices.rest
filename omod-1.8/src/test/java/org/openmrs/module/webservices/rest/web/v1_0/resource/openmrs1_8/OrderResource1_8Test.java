/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.OrderResource1_8;

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
	@SuppressWarnings( { "unchecked", "rawtypes" })
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
	
}
