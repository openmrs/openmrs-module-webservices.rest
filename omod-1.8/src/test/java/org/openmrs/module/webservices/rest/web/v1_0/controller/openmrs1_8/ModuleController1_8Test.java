/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 *
 */
public class ModuleController1_8Test extends MainResourceControllerTest {
	
	@BeforeClass
	public static void init() throws Exception {
		ModuleFactory.loadModule(new Module("Atlas Module", "atlas", "name", "author", "description", "version"), true);
		ModuleFactory.loadModule(new Module("Open Concept Lab Module", "openconceptlab", "name", "author", "description",
		        "version"), true);
	}
	
	@Test
	public void shouldGetAllModules() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		List<Object> results = Util.getResultsList(result);
		
		Assert.assertNotNull(result);
		Assert.assertEquals(getAllCount(), results.size());
	}
	
	@Test
	public void shouldGetModuleByUuid() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result.get("description"));
	}
	
	@Test
	public void shouldIncludeAuthorToFullRepresentation() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		req.addParameter("v", "full");
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(PropertyUtils.getProperty(result, "author"));
	}
	
	@Override
	public String getURI() {
		return "module";
	}
	
	@Override
	public String getUuid() {
		return RestTestConstants1_8.MODULE_UUID;
	}
	
	@Override
	public long getAllCount() {
		return ModuleFactory.getLoadedModulesMap().size();
	}
}
