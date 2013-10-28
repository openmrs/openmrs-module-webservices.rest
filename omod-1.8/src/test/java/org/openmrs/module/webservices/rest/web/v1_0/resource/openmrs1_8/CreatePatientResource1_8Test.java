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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.util.HashMap;

/**
 * Integration tests for the framework that lets a resource handle an entire class hierarchy
 */
public class CreatePatientResource1_8Test extends BaseModuleWebContextSensitiveTest {

    private SimpleObject patientSimpleObject = new SimpleObject();

    private PatientResource1_8 resource;

    @Before
	public void beforeEachTests() throws Exception {
        patientSimpleObject.putAll(new ObjectMapper().readValue(getClass().getClassLoader().getResourceAsStream("create_patient.json"), HashMap.class));
		resource = (PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(Patient.class);
	}

	@Test
	public void shouldCreatePatient() throws Exception {
		SimpleObject created = (SimpleObject) resource.create(patientSimpleObject, new RequestContext());
		Assert.assertEquals("GAN33730 - Ram Kabir", created.get("display"));
	}

}
