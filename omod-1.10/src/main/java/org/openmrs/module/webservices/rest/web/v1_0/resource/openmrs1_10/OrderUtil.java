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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openmrs.CareSetting;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;

public class OrderUtil {
	
	/**
	 * Gets the inactive orders of the specified patient as of the specified date, defaults to
	 * current date if no date is specified
	 * 
	 * @param patient
	 * @param careSetting
	 * @param orderType
	 * @param asOfDate
	 * @return
	 */
	public static List<Order> getInactiveOrders(Patient patient, CareSetting careSetting, OrderType orderType, Date asOfDate) {
		List<Order> orders = Context.getOrderService().getOrders(patient, careSetting, orderType, false);
		removeActiveOrders(orders, asOfDate);
		return orders;
	}
	
	private static void removeActiveOrders(List<Order> orders, final Date asOfDate) {
		
		CollectionUtils.filter(orders, new Predicate() {
			
			@Override
			public boolean evaluate(Object object) {
				return isDiscontinued((Order) object, asOfDate);
			}
			
		});
	}
	
	private static boolean isDiscontinued(Order order, Date asOfDate) {
		if (asOfDate == null) {
			asOfDate = new Date();
		}
		
		Date effectiveEndDate = order.getEffectiveStopDate();
		if (effectiveEndDate == null) {
			return false;
		}
		
		return effectiveEndDate.before(asOfDate);
	}
}
