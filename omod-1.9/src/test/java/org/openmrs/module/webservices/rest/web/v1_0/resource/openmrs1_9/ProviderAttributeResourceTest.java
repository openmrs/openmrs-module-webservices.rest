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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Before;
import org.openmrs.ProviderAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.test.Rest19ExtTestConstants;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ProviderAttributeResource;

public class ProviderAttributeResourceTest extends BaseDelegatingResourceTest<ProviderAttributeResource, ProviderAttribute> {
	
	@Before
	public void before() throws Exception {
		executeDataSet(Rest19ExtTestConstants.TEST_DATASET);
	}
	
	@Override
	public ProviderAttribute newObject() {
		return Context.getProviderService().getProviderAttributeByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("value", getObject().getValue());
		assertPropPresent("attributeType");
		assertPropEquals("voided", getObject().getVoided());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("value", getObject().getValue());
		assertPropPresent("attributeType");
		assertPropEquals("voided", getObject().getVoided());
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		try {
			return "Joining Date - " + new SimpleDateFormat("yyyy-MM-dd").parse("2011-04-25");
		}
		catch (ParseException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String getUuidProperty() {
		return Rest19ExtTestConstants.PROVIDER_ATTRIBUTE_UUID;
	}
}
