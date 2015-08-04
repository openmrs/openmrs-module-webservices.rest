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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Tests functionality of Program CRUD by MainResourceController
 */
public class ProgramEnrollmentController1_8Test extends MainResourceControllerTest {

	protected ProgramWorkflowService service;
	protected PatientService patientService;

	@Before
	public void before() {
		this.service = Context.getProgramWorkflowService();
		this.patientService = Context.getPatientService();
	}

	/**
	 * @see MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "programenrollment";
	}

	/**
	 * @see MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return 0;
	}

	/**
	 * @see MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_8.PATIENT_PROGRAM_UUID;
	}

	@Test
	@Override
	public void shouldGetAll() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.setParameter("patient", RestTestConstants1_8.PATIENT_IN_A_PROGRAM_UUID);
		SimpleObject result = deserialize(handle(req));

		Patient patient = patientService.getPatientByUuid(RestTestConstants1_8.PATIENT_IN_A_PROGRAM_UUID);
		List<PatientProgram> patientPrograms = service.getPatientPrograms(patient, null, null, null, null, null, true);
		Assert.assertEquals(patientPrograms.size(), Util.getResultsSize(result));
	}

	@Test
	public void shouldGetTheDefaultRepresentationOfPatientProgram() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));

		Assert.assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "patient"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "program"));
	}

	@Test
	public void shouldGetReferenceRepresentationOfPatientProgram() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		req.setParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, Representation.REF.getRepresentation());
		SimpleObject result = deserialize(handle(req));

		Assert.assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
		Assert.assertNull(PropertyUtils.getProperty(result, "patient"));
	}

	@Test
	public void shouldGetAPatientProgramByUuid() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));

		PatientProgram patientProgram = service.getPatientProgramByUuid(getUuid());
		Assert.assertNotNull(result);
		Assert.assertEquals(patientProgram.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals(patientProgram.getPatient().getUuid(), ((Map) PropertyUtils.getProperty(result, "patient")).get("uuid"));
		Assert.assertEquals(patientProgram.getProgram().getUuid(), ((Map) PropertyUtils.getProperty(result, "program")).get("uuid"));
	}

	@Test
	public void shouldEnrollAPatientToAProgram() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		SimpleObject params = new SimpleObject();
		params.add("patient", RestTestConstants1_8.PATIENT_UUID);
		params.add("program", RestTestConstants1_8.PROGRAM_UUID);
		params.add("dateEnrolled", dateFormat.format(new Date()));

		MockHttpServletRequest req = newPostRequest(getURI(), params);
		SimpleObject result = deserialize(handle(req));

		Patient patient = patientService.getPatientByUuid(RestTestConstants1_8.PATIENT_IN_A_PROGRAM_UUID);
		List<PatientProgram> patientPrograms = service.getPatientPrograms(patient, null, null, null, null, null, true);
		PatientProgram newEnrollment = patientPrograms.get(patientPrograms.size() - 1);
		Assert.assertEquals(newEnrollment.getProgram().getUuid(), ((Map) result.get("program")).get("uuid"));
		Assert.assertEquals(newEnrollment.getPatient().getUuid(), ((Map) result.get("patient")).get("uuid"));
		Assert.assertNotNull(result.get("dateEnrolled"));
	}

	@Test
	public void shouldUpdateTheDatesOfAProgramEnrollment() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleObject params = new SimpleObject();
		String date = dateFormat.format(new Date());
		params.add("dateEnrolled", date);

		MockHttpServletRequest req = newPostRequest(getURI() + "/" + getUuid(), params);
		SimpleObject result = deserialize(handle(req));

		PatientProgram patientProgram = service.getPatientProgramByUuid(getUuid());
		Assert.assertNotNull(result);
		Assert.assertEquals(dateFormat.format(patientProgram.getDateEnrolled()), date);
	}

	@Test
	public void shouldUnenrollAPatientFromAProgram() throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleObject params = new SimpleObject();
		String dateString = dateFormat.format(new Date());

		params.add("dateCompleted", dateString);

		MockHttpServletRequest req = newPostRequest(getURI() + "/" + getUuid(), params);
		SimpleObject result = deserialize(handle(req));

		PatientProgram patientProgram = service.getPatientProgramByUuid(getUuid());
		Assert.assertNotNull(result);
		Assert.assertEquals(dateFormat.format(patientProgram.getDateCompleted()), dateString);

		params.add("dateEnrolled", dateString);
		MockHttpServletRequest req1 = newPostRequest(getURI() + "/" + getUuid(), params);
		SimpleObject result1 = deserialize(handle(req));

		PatientProgram patientProgram1 = service.getPatientProgramByUuid(getUuid());
		Assert.assertNotEquals(dateFormat.format(patientProgram.getDateEnrolled()), dateString);
	}

	@Test
	public void shouldVoidAPatientProgram() throws Exception {
		PatientProgram patientProgram= service.getPatientProgramByUuid(getUuid());
		Assert.assertTrue(!patientProgram.isVoided());

		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("!purge", "random reason")));

		patientProgram = service.getPatientProgramByUuid(getUuid());
		Assert.assertTrue(patientProgram.isVoided());
	}

	@Test
	public void shouldPurgeAPatientProgram() throws Exception {
		Assert.assertNotNull(service.getPatientProgramByUuid(getUuid()));
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("purge", "")));
		Assert.assertNull(service.getPatientProgramByUuid(getUuid()));
	}


}
