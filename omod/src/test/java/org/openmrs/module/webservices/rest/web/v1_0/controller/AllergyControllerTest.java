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

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.activelist.Allergy;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.resource.AllergyResource;
import org.openmrs.module.webservices.rest.web.v1_0.resource.ResourceTestConstants;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.WebRequest;

public class AllergyControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private static final String ACTIVE_LIST_INITIAL_XML = "org/openmrs/api/include/ActiveListTest.xml";
	
	private MockHttpServletRequest emptyRequest() {
		return new MockHttpServletRequest();
	}
	
	@Before
	public void init() throws Exception {
		executeDataSet(ACTIVE_LIST_INITIAL_XML);
	}
	
	/**
	 * @see AllergyController#getAllergy(String,WebRequest)
	 * @verifies get a default representation of an allergy
	 */
	@Test
	public void getAllergy_shouldGetADefaultRepresentationOfAnAllergy() throws Exception {
		Object result = new AllergyController().retrieve("1", emptyRequest());
		Assert.assertNotNull(result);
		Util.log("Allergy fetched (default)", result);
		Assert.assertEquals(ResourceTestConstants.ALLERGY_UUID, PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "allergen"));
	}
	
	/**
	 * @see AllergyController#getAllergy(String,WebRequest)
	 * @verifies get a full representation of an allergy
	 */
	@Test
	public void getAllergy_shouldGetAFullRepresentationOfAnAllergy() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		Object result = new AllergyController().retrieve("1", req);
		Assert.assertNotNull(result);
		Util.log("Allergy fetched (default)", result);
		Assert.assertEquals(ResourceTestConstants.ALLERGY_UUID, PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "allergen"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	/**
	 * @see AllergyController#voidAllergy(String,String,WebRequest,HttpServletResponse)
	 * @verifies void an allergy
	 */
	@Test
	public void voidAllergy_shouldVoidAnAllergy() throws Exception {
		Allergy allergy = Context.getPatientService().getAllergy(1);
		Assert.assertFalse(allergy.isVoided());
		new AllergyController().delete("1", "unit test", emptyRequest(), new MockHttpServletResponse());
		allergy = Context.getPatientService().getAllergy(1);
		Assert.assertTrue(allergy.isVoided());
		Assert.assertEquals("unit test", allergy.getVoidReason());
	}
	
	/**
	 * @see AllergyController#purgeAllergy(String,WebRequest,HttpServletResponse)
	 * @verifies purge a simple allergy
	 */
	@Test
	public void purgeAllergy_shouldPurgeASimpleAllergy() throws Exception {
		Assert.assertNull(Context.getPatientService().getAllergy(1).getEndDate());
		new AllergyController().purge("1", emptyRequest(), new MockHttpServletResponse());
		Assert.assertNotNull(Context.getPatientService().getAllergy(1).getEndDate());
	}
	
	/**
	 * @see AllergyResource#getAllergyByPatient(String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void searchByPatient_shouldGetAllergyForAPatient() throws Exception {
		SimpleObject search = new AllergyController().searchByPatient("da7f524f-27ce-4bb2-86d6-6d1d05312bd5",
		    emptyRequest(), new MockHttpServletResponse());
		List<Object> results = (List<Object>) search.get("results");
		Assert.assertEquals(1, results.size());
		Assert.assertNull(PropertyUtils.getProperty(results.get(0), "uuid"));
	}
}
