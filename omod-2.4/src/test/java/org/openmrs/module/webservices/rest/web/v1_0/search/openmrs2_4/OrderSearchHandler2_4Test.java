/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs2_4;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class OrderSearchHandler2_4Test extends RestControllerTestUtils {
	
	protected String getURI() {
		return "order";
	}
	
	/**
	 * @verifies returns orders matching autoExpireOnOrBeforeDate
	 * @see OrderSearchHandler2_4#search(RequestContext)
	 */
	@Test
	public void getSearchConfig_shouldReturnOrdersAutoExpiredBeforeDate() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("s", "default");
		req.addParameter("autoExpireOnOrBeforeDate", "2008-09-30");
		
		SimpleObject result = deserialize(handle(req));
		List<Order> orders = result.get("results");
		Assert.assertEquals(4, orders.size());
	}
	
	/**
	 * @verifies returns orders with dateStopped not null
	 * @see OrderSearchHandler2_4#search(RequestContext)
	 */
	@Test
	public void getSearchConfig_shouldReturnStoppedOrders() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("s", "default");
		req.addParameter("isStopped", "true");
		
		SimpleObject result = deserialize(handle(req));
		List<Order> orders = result.get("results");
		Assert.assertEquals(4, orders.size());
	}
	
	/**
	 * @verifies returns orders matching autoExpireOnOrBeforeDate
	 * @see OrderSearchHandler2_4#search(RequestContext)
	 */
	@Test
	public void getSearchConfig_shouldReturnOnlyCanceledOrAutoExpiredBeforeDate() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("s", "default");
		req.addParameter("canceledOrExpiredOnOrBeforeDate", "2008-09-30");
		
		SimpleObject result = deserialize(handle(req));
		List<Order> orders = result.get("results");
		Assert.assertEquals(7, orders.size());
	}
	
	/**
	 * @verifies returns orders matching fulfillerStatus
	 * @see OrderSearchHandler2_4#search(RequestContext)
	 */
	@Test
	public void getSearchConfig_shouldReturnOrdersWithFulfillerStatusCompleted() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("s", "default");
		req.addParameter("fulfillerStatus", "COMPLETED");
		
		SimpleObject result = deserialize(handle(req));
		List<Order> orders = result.get("results");
		Assert.assertEquals(1, orders.size());
	}
	
	/**
	 * @verifies returns orders exluding Canceled and Expired
	 * @see OrderSearchHandler2_4#search(RequestContext)
	 */
	@Test
	public void getSearchConfig_shouldNotReturnCanceledOrExpired() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("s", "default");
		req.addParameter("excludeCanceledAndExpired", "true");
		
		SimpleObject result = deserialize(handle(req));
		List<Order> orders = result.get("results");
		Assert.assertEquals(6, orders.size());
	}
	
	/**
	 * @verifies returns orders matching action
	 * @see OrderSearchHandler2_4#search(RequestContext)
	 */
	@Test
	public void getSearchConfig_shouldReturnDiscontinuedOrders() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("s", "default");
		req.addParameter("action", "DISCONTINUE");
		
		SimpleObject result = deserialize(handle(req));
		List<Order> orders = result.get("results");
		Assert.assertEquals(2, orders.size());
	}
	
}
