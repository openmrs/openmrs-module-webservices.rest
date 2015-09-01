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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.openmrs.GlobalProperty;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class SystemSettingtResource1_9Test extends BaseDelegatingResourceTest<SystemSettingResource1_9, GlobalProperty> {
	
	@Override
	public GlobalProperty newObject() {
            return Context.getAdministrationService().getGlobalPropertyByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("property");
		assertPropPresent("value");
		assertPropPresent("description");
                assertPropEquals("display", getDisplayProperty());

	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropPresent("property");
		assertPropPresent("value");
		assertPropPresent("description");
                assertPropEquals("display", getDisplayProperty());
                assertPropPresent("datatypeClassname");
                assertPropPresent("datatypeConfig");
                assertPropPresent("preferredHandlerClassname");
                assertPropPresent("handlerConfig");
	}
	
	@Override
	public String getDisplayProperty() {
            
		return "Locale - Allowed List = en";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_9.GLOBAL_PROPERTY_UUID;
	}
}
