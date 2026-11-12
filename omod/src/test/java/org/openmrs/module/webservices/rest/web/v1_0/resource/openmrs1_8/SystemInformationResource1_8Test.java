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

import org.springframework.mock.web.MockHttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;
import org.openmrs.api.AdministrationService;
import java.util.Map;
import org.springframework.mock.web.MockHttpServletRequest;

public class SystemInformationResource1_8Test extends BaseModuleWebContextSensitiveTest {
	
	private AdministrationService administrationService;
	
	@Autowired
	private MainResourceController mainResourceController;
	
	public String getURI() {
		return "systeminformation";
	}
	
	@Test
	public void testGetAll() throws Exception {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");
		MockHttpServletResponse response = new MockHttpServletResponse();
		SimpleObject result = mainResourceController.get(getURI(), request, response);
		
		Map<String, Map<String, String>> systemInfo = result.get("systemInfo");
		
		// Check openmrsInformation
		Map<String, String> openmrsInformation = systemInfo.get("SystemInfo.title.openmrsInformation");
		Assertions.assertTrue(systemInfo.containsKey("SystemInfo.title.openmrsInformation"));
		// Check openmrsInformation Property
		Assertions.assertTrue(openmrsInformation.containsKey("SystemInfo.OpenMRSInstallation.systemDate"));
		Assertions.assertTrue(openmrsInformation.containsKey("SystemInfo.OpenMRSInstallation.systemTime"));
		Assertions.assertTrue(openmrsInformation.containsKey("SystemInfo.OpenMRSInstallation.openmrsVersion"));
		
		// Check javaRuntimeEnvironmentInformation
		Map<String, String> javRuntime = systemInfo.get("SystemInfo.title.javaRuntimeEnvironmentInformation");
		Assertions.assertTrue(systemInfo.containsKey("SystemInfo.title.javaRuntimeEnvironmentInformation"));
		// Check javaRuntimeEnvironmentInformation Property
		Assertions.assertTrue(javRuntime.containsKey("SystemInfo.JavaRuntimeEnv.operatingSystem"));
		Assertions.assertTrue(javRuntime.containsKey("SystemInfo.JavaRuntimeEnv.operatingSystemArch"));
		Assertions.assertTrue(javRuntime.containsKey("SystemInfo.JavaRuntimeEnv.operatingSystemVersion"));
		Assertions.assertTrue(javRuntime.containsKey("SystemInfo.JavaRuntimeEnv.javaVersion"));
		Assertions.assertTrue(javRuntime.containsKey("SystemInfo.JavaRuntimeEnv.javaVendor"));
		
		// Check memoryInformation
		Map<String, String> memoryInformation = systemInfo.get("SystemInfo.title.memoryInformation");
		Assertions.assertTrue(systemInfo.containsKey("SystemInfo.title.memoryInformation"));
		// Check memoryInformation Property
		Assertions.assertTrue(memoryInformation.containsKey("SystemInfo.Memory.totalMemory"));
		Assertions.assertTrue(memoryInformation.containsKey("SystemInfo.Memory.freeMemory"));
		Assertions.assertTrue(memoryInformation.containsKey("SystemInfo.Memory.maximumHeapSize"));
		
		// Check dataBaseInformation
		Map<String, String> dataBaseInformation = systemInfo.get("SystemInfo.title.dataBaseInformation");
		Assertions.assertTrue(systemInfo.containsKey("SystemInfo.title.dataBaseInformation"));
		// Check dataBaseInformation Property
		Assertions.assertTrue(dataBaseInformation.containsKey("SystemInfo.Database.name"));
		Assertions.assertTrue(dataBaseInformation.containsKey("SystemInfo.Database.connectionURL"));
		Assertions.assertTrue(dataBaseInformation.containsKey("SystemInfo.Database.userName"));
		Assertions.assertTrue(dataBaseInformation.containsKey("SystemInfo.Database.driver"));
		Assertions.assertTrue(dataBaseInformation.containsKey("SystemInfo.Database.dialect"));
		
		// Check moduleInformation
		Map<String, String> moduleInformation = systemInfo.get("SystemInfo.title.moduleInformation");
		Assertions.assertTrue(systemInfo.containsKey("SystemInfo.title.moduleInformation"));
		// Check moduleInformation Property
		Assertions.assertTrue(moduleInformation.containsKey("SystemInfo.Module.repositoryPath"));
	}
}
