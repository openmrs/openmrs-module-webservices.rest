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

import org.openmrs.CareSetting;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_10;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class CareSettingResource1_10Test extends BaseDelegatingResourceTest<CareSettingResource1_10, CareSetting> {
	
	@Override
	public CareSetting newObject() {
		return Context.getOrderService().getCareSettingByUuid(getUuidProperty());
	}
	
	@Override
	public String getDisplayProperty() {
		return "OUTPATIENT";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_10.CARE_SETTING_UUID;
	}
	
}
