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

import org.openmrs.OrderType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_10;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class OrderTypeResource1_10Test extends BaseDelegatingResourceTest<OrderTypeResource1_10, OrderType> {
	
	@Override
	public OrderType newObject() {
		return Context.getOrderService().getOrderTypeByUuid(getUuidProperty());
	}
	
	@Override
	public String getDisplayProperty() {
		return "Test order";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_10.ORDER_TYPE_UUID;
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("javaClassName", getObject().getJavaClassName());
		assertPropEquals("retired", getObject().isRetired());
		assertPropPresent("parent");
		assertPropPresent("conceptClasses");
		assertPropNotPresent("auditInfo");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("javaClassName", getObject().getJavaClassName());
		assertPropEquals("retired", getObject().isRetired());
		assertPropPresent("parent");
		assertPropPresent("conceptClasses");
		assertPropPresent("auditInfo");
		
	}
	
}
