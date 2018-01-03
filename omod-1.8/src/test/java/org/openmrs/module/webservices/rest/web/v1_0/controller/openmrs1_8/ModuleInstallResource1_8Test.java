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

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.openmrs.module.Module;
import org.openmrs.module.webservices.helper.ModuleInstall;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.MockModuleFactoryWrapper;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.response.IllegalRequestException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ModuleInstallResource1_8;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;

public class ModuleInstallResource1_8Test extends MainResourceControllerTest {
	
	@Autowired
	RestService restService;
	
	private Module mockModuleToLoad = new Module("MockModule", "mockModule", "name", "author", "description", "version");
	
	MockModuleFactoryWrapper mockModuleFactory = new MockModuleFactoryWrapper();
	
	@Before
	public void setUp() throws Exception {
		ModuleInstallResource1_8 resource = (ModuleInstallResource1_8) restService
		        .getResourceBySupportedClass(ModuleInstall.class);
		resource.setModuleFactoryWrapper(mockModuleFactory);
	}
	
	@Test
	public void shouldLoadModule() throws Exception {
		mockModuleFactory.loadModuleMock = mockModuleToLoad;
		SimpleObject simpleObject = deserialize(handle(newPostRequest(getURI(),
		    "{\"installUri\":\"" + getInstallUri() + "\", \"moduleUuid\":\"" + getUuid()
		            + "\"}")));
		
		assertThat(mockModuleFactory.loadedModules, hasItem(mockModuleToLoad));
		assertThat(mockModuleFactory.startedModules, hasItem(mockModuleToLoad));
		Assert.assertEquals(simpleObject.get("moduleUuid"), getUuid());
		Assert.assertEquals(simpleObject.get("installUri"), getInstallUri());
		
	}
	
	@Test(expected = IllegalRequestException.class)
	public void shouldThrowErrorOnPoorUri() throws Exception {
		deserialize(handle(newPostRequest(getURI(), "{\"installUri\":\"anystring\", \"moduleUuid\":\"" + getUuid() + "\"}")));
	}
	
	//ModuleUpdate resource does not support these operations
	@Override
	@Test(expected = Exception.class)
	public void shouldGetDefaultByUuid() throws Exception {
		super.shouldGetDefaultByUuid();
	}
	
	@Override
	@Test(expected = Exception.class)
	public void shouldGetRefByUuid() throws Exception {
		super.shouldGetRefByUuid();
	}
	
	@Override
	@Test(expected = Exception.class)
	public void shouldGetFullByUuid() throws Exception {
		super.shouldGetFullByUuid();
	}
	
	@Override
	@Test(expected = Exception.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
	
	@Override
	public String getURI() {
		return "moduleinstall";
	}
	
	@Override
	public String getUuid() {
		return "XForms";
	}
	
	@Override
	public long getAllCount() {
		return 0;
	}
	
	public String getInstallUri() {
		return "https://dl.bintray.com/openmrs/omod/xforms-4.3.11.omod";
	}
	
}
