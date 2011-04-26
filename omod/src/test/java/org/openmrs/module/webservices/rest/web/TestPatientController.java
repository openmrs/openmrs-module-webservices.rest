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
package org.openmrs.module.webservices.rest.web;

import org.junit.Test;
import org.openmrs.module.webservices.rest.web.controller.PatientController;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Test the {@link PatientController}
 */
public class TestPatientController extends BaseModuleContextSensitiveTest {
	
	/**
	 * Test that an error queue item can get into the database
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldGetPerson() throws Exception {
		
		initializeInMemoryDatabase();
		
		authenticate();
		
	}

}