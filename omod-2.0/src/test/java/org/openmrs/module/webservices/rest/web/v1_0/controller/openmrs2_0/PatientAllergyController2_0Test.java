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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_0;

import org.openmrs.Allergy;
import org.openmrs.Allergen;
import org.openmrs.AllergenType;
import org.openmrs.Allergies;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants2_0;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.WebRequest;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
/*
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0.PatientAllergyResource2_0;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_11.ConceptResource1_11;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.api.RestService;
*/

public class PatientAllergyController2_0Test extends MainResourceControllerTest {
	
	@Before
	public void init() throws Exception {
		executeDataSet(RestTestConstants2_0.ALLERGY_TEST_DATA_XML);
	}
	
	@Override
	public String getURI() {
		return "patient/" + RestTestConstants2_0.PATIENT_UUID + "/allergy";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants2_0.ALLERGY_UUID; // allergy does not support uuid
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return 4;
	}
	
	@Test
	public void deleteUniqueAllergy() throws Exception
	{
		Allergy allergy = Context.getPatientService().getAllergyByUuid(getUuid());
		Assert.assertFalse(allergy.isVoided());
		
		handle(newDeleteRequest(getURI() + "/" + getUuid(), 
				new Parameter("reason", "unit test")));
		allergy = Context.getPatientService().getAllergyByUuid(getUuid());
		Assert.assertTrue(allergy.isVoided());
		Assert.assertEquals("unit test", allergy.getVoidReason());
	}

	@Test
	public void deleteAllAllergies() throws Exception
	{
		handle(newDeleteRequest(getURI(), 
				new Parameter("reason", "unit test")));
		Allergy allergy = Context.getPatientService().getAllergyByUuid(getUuid());
		Allergies allergies = Context.getPatientService().getAllergies(allergy.getPatient());
		Assert.assertTrue(allergy.isVoided());
		Assert.assertEquals("unit test", allergy.getVoidReason());
		Assert.assertEquals(Allergies.UNKNOWN, allergies.getAllergyStatus());
	}
	
	@Test
	public void saveAllergy_CodedAllergen() throws Exception
	{
		Allergy allergy = Context.getPatientService().getAllergyByUuid(getUuid());
		Patient patient = allergy.getPatient();

		String json = "{\"allergen\": {\"allergenType\":\"DRUG\", \"codedAllergen\": { \"uuid\": \"35d3346a-6769-4d52-823f-b4b234bac3e3\"} }}";
		handle(newPostRequest(getURI(), json));
		
		Allergies allergies = Context.getPatientService().getAllergies(patient);
		allergies = Context.getPatientService().getAllergies(patient);
		
        Assert.assertEquals(allergies.size(), 5);
	}
	
	@Test
	public void saveAllergy_NonCodedAllergen() throws Exception
	{
		Allergy allergy = Context.getPatientService().getAllergyByUuid(getUuid());
		Patient patient = allergy.getPatient();
		
		String json = "{\"allergen\": {\"allergenType\":\"DRUG\", \"nonCodedAllergen\": \"test non coded\" }}";
		handle(newPostRequest(getURI(), json));
		
		Allergies allergies = Context.getPatientService().getAllergies(patient);
		allergies = Context.getPatientService().getAllergies(patient);
		
        Assert.assertEquals(allergies.size(), 5);
	}

}