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

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.openmrs.Allergy;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0.RestTestConstants2_0;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.WebRequest;

// TODO: implement PatientService getAllergyByUuid
// allergyapi implementation does not support getAllergyByUuid
// AllergyResource is now ineffective
@Ignore("TODO: implement PatientService getAllergyByUuid")
public class AllergyController2_0Test extends MainResourceControllerTest {

	private static final String ACTIVE_LIST_INITIAL_XML = "customAllergyTestData.xml";

	@Before
	public void init() throws Exception {
		executeDataSet(ACTIVE_LIST_INITIAL_XML);
	}

	/**
	 * @see AllergyController#getAllergy(String,WebRequest)
	 * @verifies get a default representation of an allergy
	 */
	@Test
	public void getAllergy_shouldGetADefaultRepresentationOfAnAllergy()
			throws Exception {
		String URL = getURI() + "/" + RestTestConstants2_0.ALLERGY_UUID;
		SimpleObject result = deserialize(handle(newGetRequest(URL)));
		// TODO : PatientService does not support getAllergyByUuid
		Assert.assertNull(result);
		/*
		Util.log("Allergy fetched (default)", result);
		Assert.assertEquals(RestTestConstants2_0.ALLERGY_UUID, PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "patient"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "allergen"));
		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
		*/
	}

	/**
	 * @see AllergyController#getAllergy(String,WebRequest)
	 * @verifies get a full representation of an allergy
	 */
	@Test
	public void getAllergy_shouldGetAFullRepresentationOfAnAllergy()
			throws Exception {
		String URL = getURI() + "/" + RestTestConstants2_0.ALLERGY_UUID;
		SimpleObject result = deserialize(handle(newGetRequest(URL, new Parameter(
				RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION,
				RestConstants.REPRESENTATION_FULL))));
		// TODO : PatientService does not support getAllergyByUuid
		Assert.assertNull(result);
		/*
		Util.log("Allergy fetched (full)", result);
		Assert.assertEquals(RestTestConstants2_0.ALLERGY_UUID, PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "patient"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "allergen"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
		*/
	}

	/**
	 * @see AllergyController#voidAllergy(String,String,WebRequest,HttpServletResponse)
	 * @verifies void an allergy
	 */
	@Test
	public void voidAllergy_shouldVoidAnAllergy() throws Exception {
		Allergy allergy = Context.getPatientService().getAllergy(1);
		Assert.assertFalse(allergy.isVoided());

		// TODO : PatientService does not support getAllergyByUuid
		/*
		Assert.assertNull(result);
		handle(newDeleteRequest(getURI() + "/"
				+ RestTestConstants2_0.ALLERGY_UUID, new Parameter("reason",
				"unit test")));
		allergy = Context.getPatientService().getAllergy(1);
		Assert.assertTrue(allergy.isVoided());
		Assert.assertEquals("unit test", allergy.getVoidReason());
		*/
	}

	/**
	 * @see AllergyResource2_0#getAllergyByPatient(String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 * @throws Exception
	 */
	@Test
	public void searchByPatient_shouldGetAllergyForAPatient() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter(
				"patient", "da7f524f-27ce-4bb2-86d6-6d1d05312bd5"))));
		List<Object> results = Util.getResultsList(result);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(RestTestConstants2_0.ALLERGY_UUID,
				PropertyUtils.getProperty(results.get(0), "uuid"));
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "allergy";
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants2_0.ALLERGY_UUID;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		// TODO : PatientService does not support getAllergyByUuid
		return 0;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#shouldGetAll()
	 */
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
}
