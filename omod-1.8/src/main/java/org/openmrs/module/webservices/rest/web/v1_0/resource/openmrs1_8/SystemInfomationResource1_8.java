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

import org.openmrs.api.impl.AdministrationServiceImpl;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.Listable;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

// the framework requires we specify a supportedClass, even though this shouldn't have one
@Resource(name = RestConstants.VERSION_1 + "/systeminformation", supportedClass = AdministrationServiceImpl.class, supportedOpenmrsVersions = {
        "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*" })
public class SystemInfomationResource1_8 implements Listable {
	
	@Override
	public SimpleObject getAll(RequestContext context) throws ResponseException {
		
		SimpleObject rest = new SimpleObject();
		try {
			rest.put("SystemInfo", Context.getAdministrationService().getSystemInformation());
		}
		catch (Exception ex) {
			System.out.println("SystemInfo Resource is getting Error");
		}
		
		return rest;
	}
	
	@Override
	public String getUri(Object instance) {
		return RestConstants.URI_PREFIX + "/systeminformation";
	}
}
