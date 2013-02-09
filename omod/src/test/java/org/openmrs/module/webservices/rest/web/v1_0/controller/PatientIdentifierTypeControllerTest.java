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
package org.openmrs.module.webservices.rest.web.v1_0.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.patient.impl.VerhoeffIdentifierValidator;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Tests functionality of {@link PatientIdentifierTypeController}. This does not use @should
 * annotations because the controller inherits those methods from a subclass
 */
public class PatientIdentifierTypeControllerTest extends BaseModuleWebContextSensitiveTest {
	
	//	String idTypeUuid = "1a339fe9-38bc-4ab3-b180-320988c0b968";
	//	
	//	private PatientService service;
	//	
	//	private PatientIdentifierTypeController controller;
	//	
	//	private MockHttpServletRequest request;
	//	
	//	private HttpServletResponse response;
	//	
	//	@Before
	//	public void before() {
	//		this.service = Context.getPatientService();
	//		this.controller = new PatientIdentifierTypeController();
	//		this.request = new MockHttpServletRequest();
	//		this.response = new MockHttpServletResponse();
	//	}
	//	
	//	@Test
	//	public void shouldGetOne() throws Exception {
	//		Object result = controller.retrieve(idTypeUuid, request);
	//		Assert.assertNotNull(result);
	//		Util.log("Patient Identifier Type fetched (default)", result);
	//		Assert.assertEquals(idTypeUuid, PropertyUtils.getProperty(result, "uuid"));
	//		Assert.assertEquals("OpenMRS Identification Number", PropertyUtils.getProperty(result, "name"));
	//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
	//	}
	//	
	//	@Test
	//	public void shouldListAll() throws Exception {
	//		SimpleObject result = controller.getAll(request, response);
	//		Util.log("All non-retired identifier types", result);
	//		Assert.assertNotNull(result);
	//		Assert.assertEquals(2, Util.getResultsSize(result));
	//	}
	//	
	//	@Test
	//	public void shouldCreate() throws Exception {
	//		int before = service.getAllPatientIdentifierTypes().size();
	//		String json = "{ \"name\":\"My Type\", \"description\":\"My Way\", \"required\":true, \"checkDigit\":true, \"validator\":\""
	//		        + VerhoeffIdentifierValidator.class.getName() + "\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		Object created = controller.create(post, request, response);
	//		Util.log("Created", created);
	//		int after = service.getAllPatientIdentifierTypes().size();
	//		Assert.assertEquals(before + 1, after);
	//	}
	//	
	//	@Test
	//	public void shouldUpdate() throws Exception {
	//		String json = "{ \"description\":\"something new\" }";
	//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
	//		controller.update(idTypeUuid, post, request, response);
	//		PatientIdentifierType updated = service.getPatientIdentifierTypeByUuid(idTypeUuid);
	//		Util.log("Updated", updated);
	//		Assert.assertNotNull(updated);
	//		Assert.assertEquals("OpenMRS Identification Number", updated.getName());
	//		Assert.assertEquals("something new", updated.getDescription());
	//	}
	//	
	//	@Test
	//	public void shouldDelete() throws Exception {
	//		PatientIdentifierType idType = service.getPatientIdentifierTypeByUuid(idTypeUuid);
	//		Assert.assertFalse(idType.isRetired());
	//		controller.delete(idTypeUuid, "unit test", request, response);
	//		idType = service.getPatientIdentifierTypeByUuid(idTypeUuid);
	//		Assert.assertTrue(idType.isRetired());
	//		Assert.assertEquals("unit test", idType.getRetireReason());
	//	}
	//	
	//	@Test(expected = Exception.class)
	//	// should fail to purge an item referenced by other data
	//	public void shouldFailToPurge() throws Exception {
	//		Number before = (Number) Context.getAdministrationService().executeSQL(
	//		    "select count(*) from patient_identifier_type", true).get(0).get(0);
	//		controller.purge(idTypeUuid, request, response);
	//		Context.flushSession();
	//		Number after = (Number) Context.getAdministrationService().executeSQL(
	//		    "select count(*) from patient_identifier_type", true).get(0).get(0);
	//		Assert.assertEquals(before.intValue() - 1, after.intValue());
	//	}
	//	
	//	/**
	//	 * @see PatientIdentifierTypeController#create(SimpleObject, HttpServletRequest, HttpServletResponse) 
	//	 * @verifies create a new patient identifier type
	//	 */
	//	@Test
	//	public void createPatientIdentifierType_shouldCreateANewPatientIdentifierType() throws Exception {
	//		int before = service.getAllPatientIdentifierTypes().size();
	//		String json = "{ \"name\":\"Old Identification Number\", \"description\":\"Unique number used in OpenMRS\", \"checkDigit\":true, \"required\":true, \"format\":\"\", \"formatDescription\":\"NULL\",\"validator\":\""
	//		        + VerhoeffIdentifierValidator.class.getName() + "\" }";
	//		SimpleObject post = SimpleObject.parseJson(json);
	//		Object created = controller.create(post, request, new MockHttpServletResponse());
	//		Util.log("Created patient identifier type", created);
	//		int after = service.getAllPatientIdentifierTypes().size();
	//		Assert.assertEquals(before + 1, after);
	//	}
	//	
	//	/**
	//	 * @see PatientIdentifierTypeController#update(String, SimpleObject, HttpServletRequest, HttpServletResponse)
	//	 * @verifies overwrite name on a patient identifier type
	//	 */
	//	@Test
	//	public void updatePatientIdentifierType_shouldOverwriteNameOnAPatientIdentifierType() throws Exception {
	//		String json = "{ \"name\":\"OpenMRS Identification Number\" }";
	//		SimpleObject post = SimpleObject.parseJson(json);
	//		controller.update(idTypeUuid, post, request, response);
	//		PatientIdentifierType updated = service.getPatientIdentifierTypeByUuid(idTypeUuid);
	//		Util.log("Updated", updated);
	//		Assert.assertNotNull(updated);
	//		Assert.assertEquals("OpenMRS Identification Number", updated.getName());
	//	}
	
	@Test
	public void fakeTest() {
		
	}
}
