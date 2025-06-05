/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_12;

import org.junit.Before;
import org.openmrs.OrderGroup;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_12;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class OrderGroupResource1_12Test extends BaseDelegatingResourceTest<OrderGroupResource1_12, OrderGroup> {
	
	@Before
	public void before() throws Exception {
		executeDataSet(RestTestConstants1_12.ORDER_GROUP_TEST_DATA_SET);
	}
	
	@Override
	public OrderGroup newObject() {
		return Context.getOrderService().getOrderGroupByUuid(getUuidProperty());
	}
	
	@Override
	public String getDisplayProperty() {
		return RestTestConstants1_12.ORDER_GROUP_DISPLAY;
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_12.ORDER_GROUP_UUID;
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("display");
		assertPropPresent("uuid");
		assertPropPresent("voided");
		assertPropPresent("patient");
		assertPropPresent("encounter");
		assertPropPresent("orders");
		assertPropPresent("orderSet");
		assertPropNotPresent("auditInfo");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("display");
		assertPropPresent("uuid");
		assertPropPresent("voided");
		assertPropPresent("patient");
		assertPropPresent("encounter");
		assertPropPresent("orders");
		assertPropPresent("orderSet");
		assertPropPresent("auditInfo");
	}
	
}
