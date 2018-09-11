/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

/**
 * Integration tests for the framework that lets a resource handle an entire class hierarchy
 */
public class CreateUpdatePatientResource1_9Test extends BaseModuleWebContextSensitiveTest {
	
	private PatientResource1_9 resource;
	
	@Before
	public void beforeEachTests() throws Exception {
		resource = (PatientResource1_9) Context.getService(RestService.class).getResourceBySupportedClass(Patient.class);
	}
	
	@Test
	public void shouldCreatePatient() throws Exception {
		SimpleObject patientSimpleObject = new SimpleObject();
		patientSimpleObject.putAll(new ObjectMapper().readValue(
		    getClass().getClassLoader().getResourceAsStream("create_patient.json"), HashMap.class));
		SimpleObject created = (SimpleObject) resource.create(patientSimpleObject, new RequestContext());
		Assert.assertEquals("id-B - Ram Kabir", created.get("display"));
	}
	
	@Test
	public void shouldUpdatePatient() throws Exception {
		SimpleObject patientSimpleObject = new SimpleObject();
		patientSimpleObject.putAll(new ObjectMapper().readValue(
		    getClass().getClassLoader().getResourceAsStream("update_patient.json"), HashMap.class));
		SimpleObject created = (SimpleObject) resource.update("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", patientSimpleObject,
		    new RequestContext());
		
		Date birthdate = (Date) ConversionUtil.convert("1979-12-08T00:00:00.000+0530", Date.class);
		
		Assert.assertEquals("101-6 - Mr. Rama Kabira Esq.", created.get("display"));
		Map<String, Object> person = (Map<String, Object>) created.get("person");
		Assert.assertEquals("F", person.get("gender"));
		Assert.assertNotNull(person.get("age"));
		Assert.assertEquals(ConversionUtil.convertToRepresentation(birthdate, Representation.DEFAULT),
		    person.get("birthdate"));
		Assert.assertEquals(false, person.get("birthdateEstimated"));
		Map preferredName = (Map) person.get("preferredName");
		Assert.assertEquals("Rama Kabira", preferredName.get("display"));
		Map preferredAddress = (Map) person.get("preferredAddress");
		Assert.assertEquals("address 1", preferredAddress.get("display"));
		List<Map> attributes = (List<Map>) person.get("attributes");
		Assert.assertEquals("Race = Muslim", attributes.get(0).get("display"));
	}
	
	@Test
	public void shouldCreatePatient_fromGET() throws Exception {
		executeDataSet("personAttributeTypeWithConcept.xml");
		SimpleObject patientSimpleObject = new SimpleObject();
		InputStream object = getClass().getClassLoader().getResourceAsStream("create_patient_from_get.json");
		patientSimpleObject.putAll(new ObjectMapper().readValue(object, HashMap.class));
		SimpleObject created = (SimpleObject) resource.create(patientSimpleObject, new RequestContext());
		Assert.assertEquals(created.get("uuid").toString(), patientSimpleObject.get("uuid").toString());
		Assert.assertEquals(created.get("uuid").toString(), "a65fc361-f6ac-40ba-8485-94749c061509");
		Assert.assertEquals(created.get("display").toString(), "id-B - Jonnea Bijo");
		Assert.assertEquals(((Map) (((ArrayList) created.get("identifiers")).get(0))).get("display").toString(),
		    "OpenMRS Identification Number = id-B");
	}
	
}
