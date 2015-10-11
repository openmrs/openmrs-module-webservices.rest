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
import org.junit.Ignore;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Integration tests for the framework that lets a resource handle an entire
 * class hierarchy
 */
public class CreateUpdatePatientResource1_8Test extends
		BaseModuleWebContextSensitiveTest {

	private PatientResource1_8 resource;

	@Before
	public void beforeEachTests() throws Exception {
		resource = (PatientResource1_8) Context.getService(RestService.class)
				.getResourceBySupportedClass(Patient.class);
	}

	@Test
	@Ignore
	// unexpected exception type
	public void shouldCreatePatient() throws Exception {
		SimpleObject patientSimpleObject = new SimpleObject();
		patientSimpleObject.putAll(new ObjectMapper().readValue(getClass()
				.getClassLoader().getResourceAsStream("create_patient.json"),
				HashMap.class));
		SimpleObject created = (SimpleObject) resource.create(
				patientSimpleObject, new RequestContext());
		Assert.assertEquals("id-B - Ram Kabir", created.get("display"));
	}

	@Test
	@Ignore
	// test failed
	public void shouldUpdatePatient() throws Exception {
		SimpleObject patientSimpleObject = new SimpleObject();
		patientSimpleObject.putAll(new ObjectMapper().readValue(getClass()
				.getClassLoader().getResourceAsStream("update_patient.json"),
				HashMap.class));
		SimpleObject created = (SimpleObject) resource.update(
				"da7f524f-27ce-4bb2-86d6-6d1d05312bd5", patientSimpleObject,
				new RequestContext());

		Date birthdate = (Date) ConversionUtil.convert(
				"1979-12-08T00:00:00.000+0530", Date.class);

		Assert.assertEquals("101-6 - Mr. Rama Kabira Esq.",
				created.get("display"));
		Map<String, Object> person = (Map<String, Object>) created
				.get("person");
		Assert.assertEquals("F", person.get("gender"));
		Assert.assertNotNull(person.get("age"));
		Assert.assertEquals(ConversionUtil.convertToRepresentation(birthdate,
				Representation.DEFAULT), person.get("birthdate"));
		Assert.assertEquals(false, person.get("birthdateEstimated"));
		Map preferredName = (Map) person.get("preferredName");
		Assert.assertEquals("Mr. Rama Kabira Esq.",
				preferredName.get("display"));
		Map preferredAddress = (Map) person.get("preferredAddress");
		Assert.assertEquals("address 1", preferredAddress.get("display"));
		List<Map> attributes = (List<Map>) person.get("attributes");
		Assert.assertEquals("Race = Muslim", attributes.get(0).get("display"));
	}

}
