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
import org.openmrs.OrderAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.RestTestConstants2_5;

public class OrderAttributeTypeResource2_5Test extends BaseDelegatingResourceTest<OrderAttributeTypeResource2_5, OrderAttributeType> {
	
	@Before
	public void before() throws Exception {
		executeDataSet(RestTestConstants2_5.TEST_DATASET);
	}
	
	@Override
	public OrderAttributeType newObject() {
		return Context.getOrderService().getOrderAttributeTypeByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("datatypeClassname", getObject().getDatatypeClassname());
		assertPropEquals("preferredHandlerClassname", getObject().getPreferredHandlerClassname());
		assertPropEquals("retired", getObject().getRetired());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("minOccurs", getObject().getMinOccurs());
		assertPropEquals("maxOccurs", getObject().getMaxOccurs());
		assertPropEquals("datatypeClassname", getObject().getDatatypeClassname());
		assertPropEquals("datatypeConfig", getObject().getDatatypeConfig());
		assertPropEquals("preferredHandlerClassname", getObject().getPreferredHandlerClassname());
		assertPropEquals("handlerConfig", getObject().getHandlerConfig());
		assertPropEquals("retired", getObject().getRetired());
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "Dispensing Location";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants2_5.ORDER_ATTRIBUTE_TYPE_UUID;
	}
}
